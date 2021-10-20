<?PHP

ini_set('display_errors', 1);
ini_set('display_startup_erros', 1);
error_reporting(E_ALL);
class Dao_ConnectionFactory {

    private $driver = "mysql";
    private $server = "localhost";
    private $user = "mestrado";
    private $pw = "mestrado123";
    private $dbName = "mestrado";
    public $connection = null;

    public function createConn() {
        $db = new PDO($this->driver . ":host=" . $this->server . ";dbname=" . $this->dbName, $this->user, $this->pw);
        $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        return $db;
    }

    public function killConn() {
        if ($this->connection != null) {
            $this->connection = null;
        }
    }

}

?>