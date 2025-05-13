package redirex.shipping.dto;

import lombok.Data;

import java.util.List;

@Data
public class WarehouseDTO {
    private Long id;
    private String name;
    private String location;
    private List<Long> orderItemIds;
}