package kitchenpos.menu.domain;

import static kitchenpos.exception.KitchenposExceptionMessage.MENU_PRICE_CANNOT_OVER_THAN_PRODUCT_PRICE;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import kitchenpos.common.Price;
import kitchenpos.exception.KitchenposException;

@Embeddable
public class MenuProducts {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "menuId")
    private List<MenuProduct> menuProducts = new ArrayList<>();

    public void add(List<MenuProduct> menuProducts) {
        this.menuProducts.addAll(menuProducts);
    }

    public void checkOverPrice(final Price price) {
        Price sum = this.menuProducts.stream()
                                     .map(this::calculatePrice)
                                     .reduce(Price.ZERO, Price::add);
        if (price.isBiggerThan(sum)) {
            throw new KitchenposException(MENU_PRICE_CANNOT_OVER_THAN_PRODUCT_PRICE);
        }
    }

    private Price calculatePrice(MenuProduct menuProduct) {
        return menuProduct.calculatePrice();
    }

    public <R> List<R> convertAll(Function<MenuProduct, R> converter) {
        return this.menuProducts.stream()
                                .map(converter)
                                .collect(Collectors.toList());
    }
}
