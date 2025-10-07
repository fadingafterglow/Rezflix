package ua.edu.ukma.springers.rezflix.domain.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ua.edu.ukma.springers.rezflix.domain.enums.UserType;

@Converter(autoApply = true)
public class UserTypeConverter implements AttributeConverter<UserType, String> {

    @Override
    public String convertToDatabaseColumn(UserType attribute) {
        return attribute.name();
    }

    @Override
    public UserType convertToEntityAttribute(String dbData) {
        return UserType.valueOf(dbData);
    }
}
