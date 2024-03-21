package edu.java.service;

import edu.java.domain.LinkType;
import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class LinkTypeService {
    private final Map<String, LinkType> linkTypeMap;

    public LinkTypeService(List<LinkType> linkTypes) {
        this.linkTypeMap = new HashMap<>();
        linkTypes.forEach(linkType -> linkTypeMap.put(linkType.getHost(), linkType));
    }

    public LinkType getType(@NotBlank String host) {
        LinkType type = linkTypeMap.get(host);
        if (type == null) {
            throw new IllegalArgumentException();
        }
        return type;
    }

}

