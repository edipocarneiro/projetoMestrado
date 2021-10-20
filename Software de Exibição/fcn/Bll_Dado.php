<?php

include(dirname(__FILE__) .'/../dao/Dao_Dado.php');

class Bll_Dado {
    public function getAll() {
        $dao = new Dao_Dado();
        return $dao->getAll();
    }
    
    public function getById($id) {
        $dao = new Dao_Dado();
        return $dao->getById($id);
    }
    
    public function getByData($data) {
        $dao = new Dao_Dado();
        return $dao->getByData($data);
    }
    
    public function getBySensoresData($data) {
        $dao = new Dao_Dado();
        return $dao->getBySensoresData($data);
    }
    
    public function getLastDate() {
        $dao = new Dao_Dado();
        return $dao->getLastDate();
    }

}
