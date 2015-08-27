<?php

class GCMController extends \BaseController {
  const URL = 'https://android.googleapis.com/gcm/send';  
  const GCM_API_KEY = "Authorization: key=AIzaSyAmhsE9Vxg0uDuN70ci6ptmvC9GYnEedpM";

  public function sendNote($registatoin_ids, $message)
  {      
    $fields = [
      'registration_ids' => $registatoin_ids,
      'data' => $message
    ];
    $headers = [
      self::GCM_API_KEY,
      'Content-Type: application/json'
    ];

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, self::URL);
    $headers = [
      self::GCM_API_KEY,
      'Content-Type: application/json'
    ];
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, self::URL);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
    $result = curl_exec($ch);
    
    if ($result === FALSE) {
      die('Curl failed: ' . curl_error($ch));
    }
    // Close connection
    curl_close($ch);
  }
  
  public function broadcast($message)
  {
    //$message = Input::get("message");
    $users = User::groupBy("gcm")->get();
    foreach ($users as $user) {
      $registatoin_ids[] = $user->gcm;
    }
    $message = ["message" => $message];
    $this->sendNote($registatoin_ids, $message);
  
    //return $users;
  }
}