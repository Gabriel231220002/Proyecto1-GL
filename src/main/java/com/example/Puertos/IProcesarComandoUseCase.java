package com.example.Puertos;

import com.example.Dominio.ComandoDTO;

public interface IProcesarComandoUseCase {

    public void ejecutarOperacion(ComandoDTO cmd);

}