<?php

class VO_Dado {
    //Atributos
    private $id;
    private $data;
    private $sensorNome;
    private $valor;
    
    function getId() {
        return $this->id;
    }

    function getData() {
        return $this->data;
    }

    function getSensorNome() {
        return $this->sensorNome;
    }

    function getValor() {
        return $this->valor;
    }

    function setId($id) {
        $this->id = $id;
    }

    function setData($data) {
        $this->data = $data;
    }

    function setSensorNome($sensorNome) {
        $this->sensorNome = $sensorNome;
    }

    function setValor($valor) {
        $this->valor = $valor;
    }

}
