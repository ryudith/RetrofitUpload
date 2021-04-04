<?php

//
// note :
// 1. change ip 10.0.2.2 to your domain if you try online website (not localhost)
// 2. please make sure you have increase "upload_max_filesize" in php.ini file if you want test upload big file
//	default "upload_max_filesize" on xampp is "40M"
//
$base_url = 'http://10.0.2.2/simple_php/';


$name = '';
if (isset($_POST['name']) && $_POST['name']) {
	$name = $_POST['name'];
}

$email = '';
if (isset($_POST['email']) && $_POST['email']) {
	$email = $_POST['email'];
}


$profile = '';
if (isset($_FILES['profile']['error']) && $_FILES['profile']['error'] === 0) {
	$new_filename = str_replace(' ', '_', $_FILES['profile']['name']);
	
	$result = move_uploaded_file($_FILES['profile']['tmp_name'], $new_filename);

	if ($result) {
		$profile = $base_url.$new_filename;
	}
}


$photos = [];
$n = count($_FILES['photo']['error']);

for ($i = 0; $i < $n; $i++) {
	if (isset($_FILES['photo']['error'][$i]) && $_FILES['photo']['error'][$i] === 0) {
		$new_filename = str_replace(' ', '_', $_FILES['photo']['name'][$i]);

		$result = move_uploaded_file($_FILES['photo']['tmp_name'][$i], $new_filename);

		if ($result) {
			$photos[$i] = $base_url.$new_filename;
		}
	}
}



header('Content-Type: application/json');
echo json_encode([
	'name' => $name,
	'email' => $email,
	'profile' => $profile,
	'photo' => $photos,
]);
?>