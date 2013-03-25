<?php
require_once __DIR__ . "/YahooOAuth.inc";

echo "<pre>";

var_dump($request_token);

var_dump($access_token);

# fetch user profile
$profile = $oauthapp->getProfile();

var_dump($profile);

echo "</pre>";

?>
