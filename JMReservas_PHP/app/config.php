<?php
if (!defined("SPECIALCONSTANT"))
die("Acceso denegado");
define('HOST', "localhost");
define('USER', "root");
define('PASSWORD', "IG3wfvzX3c");
define('DATABASE', "reservasAula");
define('PROFESORES_TABLE', "profesores");
define('AULAS_TABLE', "aulas");
define('RESERVAS_TABLE', "reservas");

define('OK', 200);
define('NOT_COMPLETED', 202);
define('CONFLICT', 409);

class Result {
var $code;
var $status;
var $message;
var $profesores;
var $aulas;
var $reservas;
var $profesoresAdmin;

function setCode($c) {$this->code = $c;}
function getCode() {return $this->code;}
function setStatus($s) {$this->status = $s;}
function getStatus() {return $this->status;}
function setMessage($m) {$this->message = $m;}
function getMessage() {return $this->message;}

function setProfesores($p) {$this->profesores = $p;}
function setAulas($a) {$this->aulas = $a;}
function setReservas($r) {$this->reservas = $r;}
function setProfesoresAdmin($p) {$this->profesoresAdmin = $p;}

function getProfesores() {return $this->profesores;}
function getAulas() {return $this->aulas;}
function getReservas() {return $this->reservas;}
function getProfesoresAdmin() {return $this->profesoresAdmin;}
}

class Aula {
	var $id;
	var $nombre;
	var $puestos;
}

class Profesor {
	var $id;
	var $nombre;
	var $apellido;
	var $usuario;
	var $contrasenya;
	var $asignatura;
	var $email;
	var $valido;
}

class ProfesorAdmin {
	var $id;
	var $nombre;
	var $apellido;
	var $asignatura;
	var $email;
}

class Reserva {
	var $id;
	var $hora;
	var $dia;
	var $mes;
	var $anyo;
	var $aula;
	var $profesor;
}

?>