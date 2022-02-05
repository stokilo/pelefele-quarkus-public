package com.sstec.qpelefele.model.converters;

import com.sstec.qpelefele.model.ListingPropertyType;
import com.sstec.qpelefele.model.ListingPurpose;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class ListingPropertyTypeConverter implements AttributeConverter<ListingPropertyType, String> {

    @Override
    public String convertToDatabaseColumn(ListingPropertyType listingPropertyType) {
        if (listingPropertyType == null) {
            return null;
        }
        return listingPropertyType.getCode();
    }

    @Override
    public ListingPropertyType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(ListingPropertyType.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}