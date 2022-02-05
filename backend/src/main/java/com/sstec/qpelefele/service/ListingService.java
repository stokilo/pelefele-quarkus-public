package com.sstec.qpelefele.service;

import com.sstec.qpelefele.model.ListingPropertyType;
import com.sstec.qpelefele.model.ListingPurpose;
import com.sstec.qpelefele.model.OIDCUser;
import com.sstec.qpelefele.model.Listing;
import com.sstec.qpelefele.model.dto.ListingDTO;
import com.sstec.qpelefele.model.exceptions.BusinessException;
import com.sstec.qpelefele.model.mapper.DTOConverter;
import com.sstec.qpelefele.model.vm.ListingSearchParamsVM;
import com.sstec.qpelefele.model.vm.ListingSearchVM;
import com.sstec.qpelefele.repository.ListingRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
@Transactional
public class ListingService {

    @Inject
    @Claim(standard = Claims.sub)
    String subject;

    @Inject
    DTOConverter listingConverter;

    @Inject
    ListingRepository listingRepository;

    public ListingDTO create(@Valid ListingDTO pListingDTO)
            throws BusinessException, ConstraintViolationException {

        Listing listing = listingConverter.convert(pListingDTO);
        if (listing.title.equals("unknown")) {
            throw new BusinessException("Unknown title not allowed");
        }

        listing.id = null;
        listing.user = OIDCUser.findByUUID(subject);
        pListingDTO.assets.forEach(asset -> listing.addS3Asset(listingConverter.convert(asset)));

        listing.persist();

        return listingConverter.convert(listing);
    }

    public ListingSearchVM fetchUserListings(Integer pageNumber, Integer pageSize) {
        OIDCUser user = OIDCUser.findByUUID(subject);
        PanacheQuery<Listing> userListingsQuery = Listing.find("user", user);

        List<Listing> userListings = userListingsQuery.page(Page.of(pageNumber, pageSize)).list();
        Integer numberOfPages = userListingsQuery.pageCount();
        Long totalCount = userListingsQuery.count();

        return new ListingSearchVM(listingConverter.convert(userListings), totalCount, numberOfPages);
    }

    public ListingSearchVM searchListings(@Valid ListingSearchParamsVM params) {
        PanacheQuery<Listing> userListingsQuery = Listing.find(
                "location_id = :locationId"
                .concat(" and price >= :minPrice and price <= :maxPrice")
                .concat(" and area >= :minArea and area <= :maxArea")
                .concat(" and property_type = cast(:propertyType AS text) and purpose = cast(:purpose AS text)"),
                Parameters.with("locationId", new BigDecimal(params.locationId))
                        .and("minPrice", new BigDecimal(params.minPrice))
                        .and("maxPrice", new BigDecimal(params.maxPrice))
                        .and("minArea", new BigDecimal(params.minArea))
                        .and("maxArea", new BigDecimal(params.maxArea))
                        .and("propertyType", ListingPropertyType.valueOf(params.propertyType).getCode())
                        .and("purpose", ListingPurpose.valueOf(params.purpose).getCode())
        );

        List<Listing> userListings = userListingsQuery.page(Page.of(Integer.parseInt(params.pageNumber),
                Integer.parseInt(params.pageSize))).list();
        Integer numberOfPages = userListingsQuery.pageCount();
        Long totalCount = userListingsQuery.count();

        return new ListingSearchVM(listingConverter.convert(userListings), totalCount, numberOfPages);
    }
}
