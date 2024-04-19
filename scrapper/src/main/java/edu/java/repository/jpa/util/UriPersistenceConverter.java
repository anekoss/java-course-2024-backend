package edu.java.repository.jpa.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.net.URI;
import org.springframework.util.StringUtils;

@Converter(autoApply = true)
public class UriPersistenceConverter implements AttributeConverter<URI, String> {

    @Override
    public String convertToDatabaseColumn(URI uri) {
        return (uri == null) ? null : uri.toString();
    }

    @Override
    public URI convertToEntityAttribute(String stringUri) {
        return (StringUtils.hasLength(stringUri) ? URI.create(stringUri.trim()) : null);
    }
}
