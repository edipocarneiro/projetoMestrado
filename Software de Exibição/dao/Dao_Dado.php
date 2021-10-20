<?php

include (dirname(__FILE__) ."/../model/VO_Dado.php");
include (dirname(__FILE__) . "/../fcn/Sql_Conect.php");

class Dao_Dado extends Dao_ConnectionFactory {
    private $conn = null;

    public function __construct() {
        $this->conn = Dao_ConnectionFactory::createConn();
    }

    public function getAll() {
        $SQL = "SELECT * FROM dado";

        $query = $this->conn->prepare($SQL);
        $query->execute();

        $hash[] = array();

        while ($r = $query->fetch()) {
            $vo = new VO_Dado();
            $vo->setId($r["id"]);
            $vo->setData($r["data"]);
            $vo->setSensorNome($r["sensorNome"]);
            $vo->setValor($r["valor"]);                              
            $hash[$vo->getId()] = $vo;
        }

        return $hash;
    }
    
     public function getByData($data) {
        $dataFinal = $data;
        list($ano, $mes, $dia) = explode('-', $data);
        $dataFinal = $ano . "-" . $mes . "-" . $dia;
        $dataFinal = date('Y-m-d', strtotime("+1 day", strtotime($dataFinal)));

        $SQL = "SELECT * FROM dado where data >= '" . $data . " 00:00:00' and data <= '" . $dataFinal . " 00:00:00' order by data desc ";

        $query = $this->conn->prepare($SQL);
        $query->bindValue(1, $data, PDO::PARAM_INT);
        $query->execute();

        $hash[] = array();

        while ($r = $query->fetch()) {
            $vo = new VO_Dado();
            $vo->setId($r["id"]);
            $vo->setData($r["data"]);
            $vo->setSensorNome($r["sensorNome"]);
            $vo->setValor($r["valor"]);                             
            $hash[$vo->getId()] = $vo;
        }

        return $hash;
    }
    
     public function getBySensoresData($data) {
        $dataFinal = $data;
        list($ano, $mes, $dia) = explode('-', $data);
        $dataFinal = $ano . "-" . $mes . "-" . $dia;
        $dataFinal = date('Y-m-d', strtotime("+1 day", strtotime($dataFinal)));

        $SQL = "SELECT distinct(sensorNome) FROM dado where data >= '" . $data . " 00:00:00' and data <= '" . $dataFinal . " 00:00:00' order by data asc ";

        $query = $this->conn->prepare($SQL);
        $query->bindValue(1, $data, PDO::PARAM_INT);
        $query->execute();

        $hash[] = array();

        while ($r = $query->fetch()) {
            $hash[] = $r["sensorNome"];
        }

        return $hash;
    }

    public function getById($id) {
        $SQL = "SELECT * FROM dado WHERE id = ?";

        $query = $this->conn->prepare($SQL);

        $query->bindValue(1, $id, PDO::PARAM_INT);
        $query->execute();

        $vo = new VO_Dado();

        while ($r = $query->fetch()) {
            $vo->setId($r["id"]);
            $vo->setData($r["data"]);
            $vo->setSensorNome($r["sensorNome"]);
            $vo->setValor($r["valor"]);                  
        }

        return $vo;
    }
    
    public function getLastDate() {
        $SQL = "select data from dado order by data desc limit 1";

        $query = $this->conn->prepare($SQL);
        $query->execute();

        $rows = $query->fetch(PDO::FETCH_NUM);

        return $rows[0];
    }

}
