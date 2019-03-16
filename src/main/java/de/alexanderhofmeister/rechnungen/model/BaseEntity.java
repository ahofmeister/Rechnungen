package de.alexanderhofmeister.rechnungen.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "gen", strategy = "increment")
    @GeneratedValue(generator = "gen")
    private Long id;

    public boolean isNew() {
        return this.id == null;
    }


    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }

        final BaseEntity other = (BaseEntity) object;

        if (this.getId() == null || other.getId() == null) {
            return false;
        }
        return this.id.equals(other.getId());
    }

    public void validateFields() throws BusinessException {

        List<String> missingFields = new ArrayList<>();

        for (Field field : this.getClass().getDeclaredFields()) {
            Class<?> typeOfField = field.getType();
            if (field.isAnnotationPresent(Required.class)) {
                field.setAccessible(true);
                Object value = null;
                try {
                    value = field.get(this);
                } catch (IllegalAccessException e) {
                    // should not happen
                }
                if (typeOfField == boolean.class && Boolean.FALSE.equals(value)
                        || typeOfField.isPrimitive() && ((Number) value).doubleValue() <= 0
                        || !typeOfField.isPrimitive()
                        && (value == null || typeOfField == String.class && "".equals(value))) {

                    String fieldLabel = field.getName();
                    if (field.isAnnotationPresent(Label.class)) {
                        fieldLabel = field.getAnnotation(Label.class).value();
                    }
                    missingFields.add(fieldLabel);
                }
            }
        }
        if (!missingFields.isEmpty()) {
            throw new BusinessException("Folgende Felder sind nicht ausgefüllt, aber notwendig:\n" + String.join(", ", missingFields));
        }
    }
}
