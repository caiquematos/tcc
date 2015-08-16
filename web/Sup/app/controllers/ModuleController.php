<?php

class ModuleController extends \BaseController {

	public function getIndex(){
    return Response::make('You can go and try /list');
	}
  
  public function anyList(){
    $json = json_decode(Input::get('json'));
    $modules = Module::whereCoordinator($json->coordinator)->get();
    foreach ($modules as $module) {
      $module->numb_of_samples = Sample::whereModule($module->id)->count();
    }
    return Response::json(["modules"=>$modules]);
  }
  
  //retreivign 30 samples
  public function anySample(){
    $json = json_decode(Input::get('json'));
    $samples = Sample::whereModule($json->module)->orderBy("created_at", "DESC")->take(30)->get();
    return Response::json(["samples"=>$samples]);
  }
  
  public function anyPackFrequency(){
    $json = json_decode(Input::get("json"));
    $module = Module::find($json->module);
    
    if ( $module ) {
      $module->packFrequency = $json->packFrequency;
      $module->save();
      $result = "success";
    } else {
      $result = "fail";
    }
    
    return Response::json(["result" => $result, "packFrequency"=>$module->packFrequency]);
  }
  
  public function anySleepTime(){
     $json = json_decode(Input::get("json"));
    $module = Module::find($json->module);
    
    if ( $module ) {
      $module->sleepTime = $json->sleepTime;
      $module->save();
      $result = "success";
    } else {
      $result = "fail";
    }
    
    return Response::json(["result" => $result, "sleepTime"=>$module->sleepTime]);
  }
  
  public function anySleepFrequency(){
     $json = json_decode(Input::get("json"));
    $module = Module::find($json->module);
    
    if ( $module ) {
      $module->sleepFrequency = $json->sleepFrequency;
      $module->save();
      $result = "success";
    } else {
      $result = "fail";
    }
    
    return Response::json(["result" => $result, "sleepFrequency"=>$module->sleepFrequency]);
  }
  
  //Set the module status, by the APP and the DEVICE
   public function anyStatus() {
    $json = json_decode(Input::get("json"));
    $module = Module::find($json->module);
    
    if ( $module ) {
      $module->status = $json->status;
      $module->save();
      $result = "success";
    } else {
      $result = "fail";
    }
    
    return Response::json(["result" => $result, "status"=>$module->status]);
  }
  
  
  //methods intended to be used by the DEVICE
  
   public function anyAddBattery(){
    $json = json_decode(Input::get("json"));
    $module = Module::find($json->module);
    
    if ( $module ) {
      $module->battery = $json->battery;
      $module->save();
      if ($module->battery == 2 || $module->battery == 1) // TODO: send push notification
      $result = "success";
    } else {
      $result = "fail";
    }
    
    return Response::json(["result" => $result, "battery"=>$module->battery]);
  }
  
  public function anyAddSample(){
    $json = json_decode(Input::get("json"));
    $module = Module::find($json->module);
    
    if ( $module ) {
      $sample = new Sample;
      $sample->module = $module->id;
      $sample->value = $json->value;
      $sample->save();
      $result = "success";
      return Response::json(["result" => $result, "sample"=>$sample->id]);
    } else {
      $result = "fail";
      return Response::json(["result" => $result]);
    }    
  }
  
}