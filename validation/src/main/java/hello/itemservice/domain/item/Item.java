package hello.itemservice.domain.item;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class Item {

    @NotNull(groups = UpdateValidationGroup.class)
    private Long id;

    @NotBlank(groups = {UpdateValidationGroup.class, SaveValidationGroup.class})
    private String itemName;

    @NotNull(groups = {SaveValidationGroup.class, UpdateValidationGroup.class})
    @Range(min = 1000, max = 1_000_000, groups = {SaveValidationGroup.class, UpdateValidationGroup.class})
    private Integer price;

    @NotNull(groups = {SaveValidationGroup.class, UpdateValidationGroup.class})
    @Max(value = 9999, groups = {SaveValidationGroup.class})
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }

    public interface SaveValidationGroup {}
    public interface UpdateValidationGroup {}
}
