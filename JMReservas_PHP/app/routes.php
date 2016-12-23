<?php
if (!defined("SPECIALCONSTANT"))
	die("Acceso denegado");
$app->get("/", function() use($app) {
	$app->response->headers->set("Content-type", "text/html; charset=utf-8");
	$app->response->status(OK);
	$app->response->body(
		"<h2><a href ='doc/index.html'>Documentación API</a></h2>");
});

/*

TODO LO RELACIONADO CON GET

*/

$app->get("/api/profesores", function() use($app) {
	$result = getProfesores();
	$app->response->status($result->getStatus());
	$app->response->body(json_encode($result));
});

function getProfesores() {
	$result = new Result();
	try {
		$connection = getConnection();
		$dbquery = $connection->prepare("SELECT * FROM " . PROFESORES_TABLE);
		$dbquery->execute();
		$profesores = $dbquery->fetchAll(PDO::FETCH_ASSOC);
		$connection = null;

		$result->setCode(TRUE);
		$result->setStatus(OK);
		$result->setProfesores($profesores);
	} catch (PDOException $e) {
		$result->setCode(FALSE);
		$result->setStatus(CONFLICT);
		$result->setMessage("Error: " . $e->getMessage());
	}
	return $result;
}

$app->get("/api/profesores/admin/:ide", function($ide) use($app) {
	$result = getProfesoresAdmin($ide);
	$app->response->status($result->getStatus());
	$app->response->body(json_encode($result));
});

function getProfesoresAdmin($ide) {
	$result = new Result();
	try {
		$connection = getConnection();
		$dbquery = $connection->prepare("SELECT id, nombre, apellido, asignatura, email FROM " . PROFESORES_TABLE . " where id <> " . $ide);
		$dbquery->execute();
		$profesoresAdmin = $dbquery->fetchAll(PDO::FETCH_ASSOC);
		$connection = null;

		$result->setCode(TRUE);
		$result->setStatus(OK);
		$result->setProfesoresAdmin($profesoresAdmin);
	} catch (PDOException $e) {
		$result->setCode(FALSE);
		$result->setStatus(CONFLICT);
		$result->setMessage("Error: " . $e->getMessage());
	}
	return $result;
}

$app->get("/api/profesores/:id", function($id) use($app) {
	$result = getProfesor($id);
	$app->response->status($result->getStatus());
	$app->response->body(json_encode($result));
});

function getProfesor($id) {
	$result = new Result();
	try {
		$connection = getConnection();
		$dbquery = $connection->prepare("SELECT * FROM " . PROFESORES_TABLE . " WHERE id = ?");
		$dbquery->bindParam(1, $id);
		$dbquery->execute();
		$profesor = $dbquery->fetchObject();
		$connection = null;

		if ($profesor != null) {
			$result->setCode(TRUE);
			$result->setStatus(OK);
			$result->setProfesores($profesor);
		}
		else {
			$result->setCode(FALSE);
			$result->setStatus(NOT_COMPLETED);
			$result->setMessage("Este profesor no existe");
		}
	} catch (PDOException $e) {
		$result->setCode(FALSE);
		$result->setStatus(CONFLICT);
		$result->setMessage("Error: " . $e->getMessage());
	}
	return $result;
}

$app->get("/api/aulas", function() use($app) {
	$result = getAulas();
	$app->response->status($result->getStatus());
	$app->response->body(json_encode($result));
});

function getAulas() {
	$result = new Result();
	try {
		$connection = getConnection();
		$dbquery = $connection->prepare("SELECT * FROM " . AULAS_TABLE);
		$dbquery->execute();
		$aulas = $dbquery->fetchAll(PDO::FETCH_ASSOC);
		$connection = null;

		$result->setCode(TRUE);
		$result->setStatus(OK);
		$result->setAulas($aulas);
	} catch (PDOException $e) {
		$result->setCode(FALSE);
		$result->setStatus(CONFLICT);
		$result->setMessage("Error: " . $e->getMessage());
	}
	return $result;
}

$app->get("/api/aulas/:numero", function($numero) use($app) {
	$result = getAula($numero);
	$app->response->status($result->getStatus());
	$app->response->body(json_encode($result));
});

function getAula($numero) {
	$result = new Result();
	try {
		$connection = getConnection();
		$dbquery = $connection->prepare("SELECT * FROM " . AULAS_TABLE . " WHERE numero = ?");
		$dbquery->bindParam(1, $numero);
		$dbquery->execute();
		$aula = $dbquery->fetchObject();
		$connection = null;

		if ($aula != null) {
			$result->setCode(TRUE);
			$result->setStatus(OK);
			$result->setAulas($aula);
		}
		else {
			$result->setCode(FALSE);
			$result->setStatus(NOT_COMPLETED);
			$result->setMessage("Este aula no existe");
		}
	} catch (PDOException $e) {
		$result->setCode(FALSE);
		$result->setStatus(CONFLICT);
		$result->setMessage("Error: " . $e->getMessage());
	}
	return $result;
}

$app->get("/api/aulas/:numero/res", function($numero) use($app) {
	$result = getAulaReservas($numero);
	$app->response->status($result->getStatus());
	$app->response->body(json_encode($result));
});

function getAulaReservas($numero) {
	$result = new Result();
	try {
		$connection = getConnection();
		//$dbquery = $connection->prepare("SELECT * FROM " . RESERVAS_TABLE . " WHERE aula = " . $numero);
		$dbquery = $connection->prepare("select r.id, r.hora, r.dia, r.mes, r.anyo, a.numero as 'id_aula', a.nombre as 'aula', p.id as 'id_profesor', p.nombre as 'profesor' from reservas r, aulas a, profesores p where r.aula = a.numero and r.profesor = p.id and a.numero = ".$numero." order by anyo, mes, dia, hora;");
		$dbquery->execute();
		$reservas = $dbquery->fetchAll(PDO::FETCH_ASSOC);
		$connection = null;

		$result->setCode(TRUE);
		$result->setStatus(OK);
		$result->setReservas($reservas);
	} catch (PDOException $e) {
		$result->setCode(FALSE);
		$result->setStatus(CONFLICT);
		$result->setMessage("Error: " . $e->getMessage());
	}
	return $result;
}

$app->get("/api/aulas/:numero/res/m/:mes", function($numero, $mes) use($app) {
	$result = getAulaReservasMes($numero, $mes);
	$app->response->status($result->getStatus());
	$app->response->body(json_encode($result));
});

function getAulaReservasMes($numero, $mes) {
	$result = new Result();
	try {
		$connection = getConnection();
		//$dbquery = $connection->prepare("SELECT * FROM " . RESERVAS_TABLE . " WHERE aula = " . $numero . " && mes = " . $mes);
		$dbquery = $connection->prepare("select r.id, r.hora, r.dia, r.mes, r.anyo, a.numero as 'id_aula', a.nombre as 'aula', p.id as 'id_profesor', p.nombre as 'profesor' from reservas r, aulas a, profesores p where r.aula = a.numero and r.profesor = p.id and a.numero = ".$numero." and r.mes = ".$mes." order by anyo, mes, dia, hora;");
		$dbquery->execute();
		$reservas = $dbquery->fetchAll(PDO::FETCH_ASSOC);
		$connection = null;

		$result->setCode(TRUE);
		$result->setStatus(OK);
		$result->setReservas($reservas);
	} catch (PDOException $e) {
		$result->setCode(FALSE);
		$result->setStatus(CONFLICT);
		$result->setMessage("Error: " . $e->getMessage());
	}
	return $result;
}

$app->get("/api/aulas/:numero/res/m/:mes/d/:dia", function($numero, $mes, $dia) use($app) {
	$result = getAulaReservasDia($numero, $mes, $dia);
	$app->response->status($result->getStatus());
	$app->response->body(json_encode($result));
});

function getAulaReservasDia($numero, $mes, $dia) {
	$result = new Result();
	try {
		$connection = getConnection();
		//$dbquery = $connection->prepare("SELECT * FROM " . RESERVAS_TABLE . " WHERE aula = " . $numero . " && mes = " . $mes . " && dia = " . $dia);
		$dbquery = $connection->prepare("select r.id, r.hora, r.dia, r.mes, r.anyo, a.numero as 'id_aula', a.nombre as 'aula', p.id as 'id_profesor', p.nombre as 'profesor' from reservas r, aulas a, profesores p where r.aula = a.numero and r.profesor = p.id and a.numero = ".$numero." and r.mes = ".$mes." and r.dia = ".$dia." order by anyo, mes, dia, hora;");
		$dbquery->execute();
		$reservas = $dbquery->fetchAll(PDO::FETCH_ASSOC);
		$connection = null;

		$result->setCode(TRUE);
		$result->setStatus(OK);
		$result->setReservas($reservas);
	} catch (PDOException $e) {
		$result->setCode(FALSE);
		$result->setStatus(CONFLICT);
		$result->setMessage("Error: " . $e->getMessage());
	}
	return $result;
}

$app->get("/api/res", function() use($app) {
	$result = getReservas();
	$app->response->status($result->getStatus());
	$app->response->body(json_encode($result));
});

function getReservas() {
	$result = new Result();
	try {
		$connection = getConnection();
		$dbquery = $connection->prepare("select r.id, r.hora, r.dia, r.mes, r.anyo, a.numero as 'id_aula', a.nombre as 'aula', p.id as 'id_profesor', p.nombre as 'profesor' from reservas r, aulas a, profesores p where r.aula = a.numero and r.profesor = p.id order by anyo, mes, dia, hora;");
		$dbquery->execute();
		$reservas = $dbquery->fetchAll(PDO::FETCH_ASSOC);
		$connection = null;

		$result->setCode(TRUE);
		$result->setStatus(OK);
		$result->setReservas($reservas);
	} catch (PDOException $e) {
		$result->setCode(FALSE);
		$result->setStatus(CONFLICT);
		$result->setMessage("Error: " . $e->getMessage());
	}
	return $result;
}

$app->get("/api/res/profesor/:num", function($num) use($app) {
	$result = getReservasProfesor($num);
	$app->response->status($result->getStatus());
	$app->response->body(json_encode($result));
});

function getReservasProfesor($num) {
	$result = new Result();
	try {
		$connection = getConnection();
		$dbquery = $connection->prepare("select r.id, r.hora, r.dia, r.mes, r.anyo, a.numero as 'id_aula', a.nombre as 'aula', p.id as 'id_profesor', p.nombre as 'profesor' from reservas r, aulas a, profesores p where r.aula = a.numero and r.profesor = p.id and p.id = ".$num." order by anyo, mes, dia, hora;");
		$dbquery->execute();
		$reservas = $dbquery->fetchAll(PDO::FETCH_ASSOC);
		$connection = null;

		$result->setCode(TRUE);
		$result->setStatus(OK);
		$result->setReservas($reservas);
	} catch (PDOException $e) {
		$result->setCode(FALSE);
		$result->setStatus(CONFLICT);
		$result->setMessage("Error: " . $e->getMessage());
	}
	return $result;
}

$app->get("/api/res/:id", function($id) use($app) {
	$result = getReserva($id);
	$app->response->status($result->getStatus());
	$app->response->body(json_encode($result));
});

function getReserva($id) {
	$result = new Result();
	try {
		$connection = getConnection();
		$dbquery = $connection->prepare("SELECT * FROM " . RESERVAS_TABLE . " WHERE id = ?");
		$dbquery->bindParam(1, $id);
		$dbquery->execute();
		$reserva = $dbquery->fetchObject();
		$connection = null;

		if ($reserva != null) {
			$result->setCode(TRUE);
			$result->setStatus(OK);
			$result->setReservas($reserva);
		}
		else {
			$result->setCode(FALSE);
			$result->setStatus(NOT_COMPLETED);
			$result->setMessage("Esta reserva no existe");
		}
	} catch (PDOException $e) {
		$result->setCode(FALSE);
		$result->setStatus(CONFLICT);
		$result->setMessage("Error: " . $e->getMessage());
	}
	return $result;
}

$app->get("/api/login/:usuario", function($usuario) use($app) {
	$result = findByName($usuario);
	$app->response->status($result->getStatus());
	$app->response->body(json_encode($result));
});

function findByName($usuario) {
	$result = new Result();
	try{
		$connection = getConnection();
		$dbquery = $connection->prepare("SELECT * FROM " . PROFESORES_TABLE . " WHERE usuario = ?");
		$dbquery->bindParam(1, $usuario);
		$dbquery->execute();
		$number = $dbquery->rowCount();
		$profesor = $dbquery->fetchAll(PDO::FETCH_ASSOC);
		$connection = null;

		if ($number > 0) {
			$result->setCode(TRUE);
			$result->setStatus(OK);
			$result->setProfesores($profesor);
		}
		else {
			$result->setCode(FALSE);
			$result->setStatus(NOT_COMPLETED);
			$result->setMessage("NOTHING FOUND");
		}
	} catch (PDOException $e) {
		$result->setCode(FALSE);
		$result->setStatus(CONFLICT);
		$result->setMessage("Error: " . $e->getMessage());
	}
	return $result;
}

/*

TODO LO RELACIONADO CON POST

*/

$app->post("/api/add", function() use($app) {
	
	$vh = $app->request->post('hora');
	$vd = $app->request->post('dia');
	$vm = $app->request->post('mes');
	$va = $app->request->post('anyo');
	$vau = $app->request->post('aula');
	$vpr = $app->request->post('profesor');
	$result = postReserva($vh, $vd, $vm, $va, $vau, $vpr);
	
	/*
	$json = $app->request->post('reserva');
	$site = json_decode($json);
	$result = postsite($reserva->name, $reserva->link, $reserva->link, $reserva->link, $reserva->link, $reserva->link,);
	*/

	$app->response->status($result->getStatus());
	$app->response->body(json_encode($result));
});

function postReserva($ho, $di, $me, $an, $au, $pr) {
	$result = new Result();
	try {
		$connection = getConnection();
		$dbquery = $connection->prepare("INSERT INTO " . RESERVAS_TABLE . " (hora, dia, mes, anyo, aula, profesor) VALUES(?, ?, ?, ?, ?, ?)");
		$dbquery->bindParam(1, $ho);
		$dbquery->bindParam(2, $di);
		$dbquery->bindParam(3, $me);
		$dbquery->bindParam(4, $an);
		$dbquery->bindParam(5, $au);
		$dbquery->bindParam(6, $pr);
		$dbquery->execute();
		$number = $dbquery->rowCount();
		$connection = null;

		if ($number > 0) {
			$result->setCode(TRUE);
			$result->setStatus(OK);
		}
		else {
			$result->setCode(FALSE);
			$result->setStatus(NOT_COMPLETED);
			$result->setMessage("NOT INSERTED");
		}
	} catch (PDOException $e) {
		$result->setCode(FALSE);
		$result->setStatus(CONFLICT);
		$result->setMessage("Error: " . $e->getMessage());
	}
	return $result;
}

$app->post("/api/mail", function() use($app) {
	$f = $app->request->post('from');
	$p = $app->request->post('password');
	$t = $app->request->post('to');
	$s = $app->request->post('subject');
	$m = $app->request->post('message');
	$result = postEmail($f, $p, $t, $s, $m);

	$app->response->status($result->getStatus());
	$app->response->body(json_encode($result));
});

function postEmail($from, $password, $to, $subject, $message) {
	header('Content-type: application/json;charset=utf8');
	require_once('phpmailer524/class.phpmailer.php');
	require_once "config.php";
	$response = new Result();

	$mail = new PHPMailer(true);
	$mail->IsSMTP();

	try {
		$mail->SMTPDebug = 0;
		$mail->SMTPAuth = true;

		//$mail->SMTPSecure = "tls";
		$mail->SMTPSecure = "ssl";
		//$mail->Host = "smtp.gmail.com";
		//$mail->Host = "smtp.openmailbox.org";
		$mail->Host = "outlook.websitewelcome.com";
		//$mail->Port = 587;
		$mail->Port = 465;

		$mail->Username = $from;
		$mail->Password = $password;
		$mail->AddAddress($to);
		$mail->SetFrom($from, 'Reservas online');
		$mail->AddReplyTo($from, 'Reservas online');
		$mail->Subject = $subject;
		$mail->AltBody = 'Message in plain text';
		$mail->MsgHTML($message);

		$mail->Send();

		$response->setCode(TRUE);
		$response->setMessage("Mensaje enviado a " . $to);
		
	} catch (phpmailerException $e) {
		$response->setCode(FALSE);
		$response->setMessage("Error: " . $e->errorMessage());
	} catch (Exception $e) {
		$response->setCode(FALSE);
		$response->setMessage("Error: " . $e->getMessage());
	}
        return $response;
}

/*

TODO LO RELACIONADO CON PUT

*/

$app->put("/api/mod/:id", function($id) use($app) {
	$h = $app->request->put('hora');
	$d = $app->request->put('dia');
	$m = $app->request->put('mes');
	$a = $app->request->put('anyo');
	$au = $app->request->put('aula');
	$pr = $app->request->put('profesor');
	$i = $app->request->put('id');
	$result = putReserva($h, $d, $m, $a, $au, $pr, $i);

	$app->response->status($result->getStatus());
	$app->response->body(json_encode($result));
});

function putReserva($ho, $di, $me, $an, $au, $pr, $id) {
	$result = new Result();
	try {
		$connection = getConnection();
		$dbquery = $connection->prepare("UPDATE " . RESERVAS_TABLE . " SET hora = ?, dia = ?, mes = ?, anyo = ?, aula = ?, profesor = ? WHERE id = ?");
		$dbquery->bindParam(1, $ho);
		$dbquery->bindParam(2, $di);
		$dbquery->bindParam(3, $me);
		$dbquery->bindParam(4, $an);
		$dbquery->bindParam(5, $au);
		$dbquery->bindParam(6, $pr);
		$dbquery->bindParam(7, $id);
		$dbquery->execute();
		$number = $dbquery->rowCount();
		$connection = null;

		if ($number > 0) {
			$result->setCode(TRUE);
			$result->setStatus(OK);
		}
		else {
			$result->setCode(FALSE);
			$result->setStatus(NOT_COMPLETED);
			$result->setMessage("NOT UPDATED");
		}
	} catch (PDOException $e) {
		$result->setCode(FALSE);
		$result->setStatus(CONFLICT);
		$result->setMessage("Error: " . $e->getMessage());
	}
	return $result;
}

/*

TODO LO RELACIONADO CON DELETE

*/

$app->delete("/api/eliminar/:eli", function($eli) use($app) {
	$result = deleteReserva($eli);
	$app->response->status($result->getStatus());
	$app->response->body(json_encode($result));
});

function deleteReserva($eli) {
	$result = new Result();
	try {
		$connection = getConnection();
		$dbquery = $connection->prepare("DELETE FROM " . RESERVAS_TABLE . " WHERE id = " . $eli);
		$dbquery->execute();
		$number = $dbquery->rowCount();
		$connection = null;

		if ($number > 0) {
			$result->setCode(TRUE);
			$result->setStatus(OK);
		}
		else {
			$result->setCode(FALSE);
			$result->setStatus(NOT_COMPLETED);
			$result->setMessage("NOT DELETED");
		}
	} catch (PDOException $e) {
		$result->setCode(FALSE);
		$result->setStatus(CONFLICT);
		$result->setMessage("Error: " . $e->getMessage());
	}
	return $result;
}