package com.sstec.qpelefele.model.vm;

import com.sstec.qpelefele.model.TypescriptSerializable;
import com.sstec.qpelefele.model.dto.LocationDTO;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.ArrayList;
import java.util.List;

@TypescriptSerializable
@RegisterForReflection
public class LocationSearchVM {
    public List<LocationDTO> locations = new ArrayList<LocationDTO>();
}
