<?php
require_once __DIR__ . "/YahooOAuth.inc";


echo "<pre>";
var_dump($request_token);
var_dump($access_token);

$url = "http://social.yahooapis.com/v1/me/guid?format=json";

echo "\nurl = $url\n";

$parameters = array();
$method = YahooCurl::GET;

$oauth_request = OAuthRequest::from_consumer_and_token($oauthapp->consumer, $oauthapp->token, $method, $url, $parameters);
$oauth_request->sign_request($oauthapp->signature_method_hmac_sha1, $oauthapp->consumer, $oauthapp->token);

$resp = $oauthapp->client->access_resource($oauth_request);

echo $resp;
echo "</pre>";
