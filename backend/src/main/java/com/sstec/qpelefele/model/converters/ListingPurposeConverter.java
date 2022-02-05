package com.sstec.qpelefele.model.converters;

import com.sstec.qpelefele.model.ListingPurpose;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class ListingPurposeConverter implements AttributeConverter<ListingPurpose, String> {

    @Override
    public String convertToDatabaseColumn(ListingPurpose listingPurpose) {
        if (listingPurpose == null) {
            return null;
        }
        return listingPurpose.getCode();
    }

    @Override
    public ListingPurpose convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(ListingPurpose.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}