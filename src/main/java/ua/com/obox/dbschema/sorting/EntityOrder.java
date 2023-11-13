package ua.com.obox.dbschema.sorting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "entity_order")
public class EntityOrder {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "CHAR(36)")
    @JsonIgnore
    private String entityId;
    @Column(columnDefinition = "CHAR(36)")
    @JsonProperty("reference_id")
    private String referenceId;
    @JsonProperty("reference_type")
    private String referenceType;
    @Lob
    @JsonIgnore
    private String sortedList;
    @JsonIgnore
    private long createdAt;
    @JsonIgnore
    private long updatedAt;
    @Transient
    @JsonProperty("sorted_list")
    String[] sortedArray;

    @JsonIgnore
    public void setSortedArray(String[] array, EntityOrderRepository entityOrderRepository) {
        for (String el : array) {
            EntityOrder entityOrder = entityOrderRepository.findBySortedListContaining(el).orElse(null);
            if (entityOrder != null) {
                String[] elements = entityOrder.getSortedList().split(",");
                StringBuilder result = new StringBuilder();
                for (String element : elements) {
                    if (!element.equals(el)) {
                        result.append(element).append(",");
                    }
                }

                if (result.length() > 0) {
                    result.setLength(result.length() - 1);
                    entityOrder.setSortedList(result.toString());
                    entityOrderRepository.save(entityOrder);
                } else {
                    entityOrderRepository.delete(entityOrder);
                }
            }
        }
        this.sortedList = String.join(",", array);
    }
}
