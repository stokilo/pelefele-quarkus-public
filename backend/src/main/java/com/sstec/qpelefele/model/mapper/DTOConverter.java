package com.sstec.qpelefele.model.mapper;

import com.sstec.qpelefele.model.Listing;
import com.sstec.qpelefele.model.ListingS3Asset;
import com.sstec.qpelefele.model.Location;
import com.sstec.qpelefele.model.dto.ListingDTO;
import com.sstec.qpelefele.model.dto.LocationDTO;
import com.sstec.qpelefele.model.dto.ListingS3AssetDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "cdi")
public interface DTOConverter {
    ListingDTO convert(Listing listing);
    Listing convert(ListingDTO listingDTO);
    List<ListingDTO> convert(List<Listing> listings);

    LocationDTO convert(Location location);
    Location convert(LocationDTO locationDTO);

    @Mapping(target = "listing", ignore = true)
    ListingS3AssetDTO convert(ListingS3Asset listingS3Asset);
    @Mapping(target = "listing", ignore = true)
    ListingS3Asset convert(ListingS3AssetDTO listingS3AssetDTO);
    @Mapping(target = "listing", ignore = true)
    List<ListingS3AssetDTO> convertList(List<ListingS3Asset> listings);
    @Mapping(target = "listing", ignore = true)
    List<ListingS3Asset> convert2List(List<ListingS3AssetDTO> listings);
}
