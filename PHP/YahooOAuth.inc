<?php
require_once 'OAuth/OAuth.php';
require_once 'Yahoo/YahooOAuthApplication.class.php';

//print_r($_SERVER);

$CONSUMER_KEY = "";
$CONSUMER_SECRET = "";
$APPLICATION_ID = "";
$CALLBACK_URL = "";

$oauthapp = new YahooOAuthApplication($CONSUMER_KEY, $CONSUMER_SECRET, $APPLICATION_ID);

session_start();

if(empty($_SESSION['my_oauth_request_token']))
{
	$request_token = $oauthapp->getRequestToken($CALLBACK_URL);
	$_SESSION['my_oauth_request_token'] = $request_token->to_string();

	$redirect_url  = $oauthapp->getAuthorizationUrl($request_token);
	header("Location: $redirect_url");
	die;
}
else 
{
	$my_oauth_request_token = $_SESSION['my_oauth_request_token'];
//echo "get request token from session<br><p>$my_oauth_request_token</p>";
	$request_token = YahooOAuthRequestToken::from_string($my_oauth_request_token);
}

if(empty($_SESSION['my_oauth_access_token']))
{
	$oauth_verifier =  $_REQUEST['oauth_verifier'];
	# Exchange request token for authorized access token
	$access_token  = $oauthapp->getAccessToken($request_token, $_REQUEST['oauth_verifier']);

	$_SESSION['my_oauth_access_token'] = $access_token->to_string();
}
else
{
	$access_token_str = $_SESSION['my_oauth_access_token'];
//echo "get access token from session<br><p>$access_token_str</p>";
	$access_token = YahooOAuthAccessToken::from_string($access_token_str);
}

# update access token
$oauthapp->token = $access_token;
