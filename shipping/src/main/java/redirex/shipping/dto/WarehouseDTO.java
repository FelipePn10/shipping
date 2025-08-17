package redirex.shipping.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class WarehouseDTO {
    private UUID id;
    private String name;
    private String location;
    private List<UUID> orderItemIds;
}