package ru.yandex.shop.model;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;

import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table("orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {

    @Id
    private Long id;

    private Long totalSum;
}
