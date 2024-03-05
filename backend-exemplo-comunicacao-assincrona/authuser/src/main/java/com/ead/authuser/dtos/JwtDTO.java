package com.ead.authuser.dtos;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor //contrutor apenas com campos obrigatórios
public class JwtDTO {

    @NonNull //anotação do lombok para validar no construtor
    private String token;
    private String type = "Bearer";

}
