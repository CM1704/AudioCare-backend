package com.audiocare.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AdminPermissionsRequest {

    @NotNull private Boolean modelRead;
    @NotNull private Boolean modelCrud;
    @NotNull private Boolean supplierOrderRead;
    @NotNull private Boolean supplierOrderCru;
    @NotNull private Boolean productRead;
    @NotNull private Boolean productCrud;
    @NotNull private Boolean movementsRead;
    @NotNull private Boolean clientRead;
    @NotNull private Boolean clientCrud;
    @NotNull private Boolean saleRead;
    @NotNull private Boolean saleCrud;
}