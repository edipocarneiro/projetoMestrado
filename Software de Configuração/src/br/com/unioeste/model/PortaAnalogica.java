/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.unioeste.model;

import java.math.BigDecimal;

/**
 *
 * @author cpS
 */
public class PortaAnalogica {

    public Integer id;
    public String identificador;
    public Boolean status;
    public BigDecimal intervaloIni;
    public BigDecimal intervaloFim;
    public BigDecimal offset;

    public PortaAnalogica(Integer id, String identificador, Boolean status, BigDecimal intervaloIni, BigDecimal intervaloFim, BigDecimal offset) {
        this.id = id;
        this.identificador = identificador;
        this.status = status;
        this.intervaloIni = intervaloIni;
        this.intervaloFim = intervaloFim;
        this.offset = offset;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public BigDecimal getOffset() {
        return offset;
    }

    public void setOffset(BigDecimal offset) {
        this.offset = offset;
    }

    public BigDecimal getIntervaloIni() {
        return intervaloIni;
    }

    public void setIntervaloIni(BigDecimal intervaloIni) {
        this.intervaloIni = intervaloIni;
    }

    public BigDecimal getIntervaloFim() {
        return intervaloFim;
    }

    public void setIntervaloFim(BigDecimal intervaloFim) {
        this.intervaloFim = intervaloFim;
    }
}
