<?php

class ModuleController extends \BaseController {
  
  const BATTERY_FULL = 5.0;
  private $history;
  
  public function ModuleController(){
    $this->history = new HistoryController;
  }

	public function getIndex(){
    $modules = Module::whereCoordinator(Input::get("coordinator"))->get();
    foreach ($modules as $module) {
      $module->numb_of_samples = Sample::whereModule($module->id)->count();
        $module->battery = ($module->battery * 100)/self::BATTERY_FULL;
    }
    return View::make('modulos',['modules'=>$modules]);
	}
  
  public function anyList(){
    $modules = Module::whereCoordinator(Input::get("coordinator"))->get();
    foreach ($modules as $module) {
      $module->numb_of_samples = Sample::whereModule($module->id)->count();
    }
    return Response::json(["modules"=>$modules]);
  }
  
  //retreivign 30 samples
  public function anySample(){
    $samples = Sample::whereModule(Input::get("module"))->orderBy("created_at", "DESC")->take(30)->get();
    return Response::json(["samples"=>$samples]);
  }
  
  public function anyPackFrequency(){
    $module = Module::find(Input::get("module"));
    
    if ( $module ) {
      $module->packFrequency = Input::get("packFrequency");
      $module->save();
      $this->history->save(Input::get("user"), "mudou a frequência de pacotes do módulo ".$module->id." para ".$module->packFrequency);
      $result = "success";
    } else {
      $result = "fail";
    }
    
    return Response::json(["result" => $result, "packFrequency"=>$module->packFrequency]);
  }
  
  public function anySleepTime(){
    $module = Module::find(Input::get("module"));
    
    if ( $module ) {
      $module->sleepTime = Input::get("sleepTime");
      $module->save();
      $this->history->save(Input::get("user"), "mudou o sleep time do módulo ".$module->id." para ".$module->sleepTime);
      $result = "success";
    } else {
      $result = "fail";
    }
    
    return Response::json(["result" => $result, "sleepTime"=>$module->sleepTime]);
  }
  
  public function anySleepFrequency(){
    $module = Module::find(Input::get("module"));
    
    if ( $module ) {
      $module->sleepFrequency = Input::get("sleepFrequency");
      $module->save();
      $this->history->save(Input::get("user"), "mudou a frequência de standby do módulo ".$module->id." para ".$module->sleepFrequency);
      $result = "success";
    } else {
      $result = "fail";
    }
    
    return Response::json(["result" => $result, "sleepFrequency"=>$module->sleepFrequency]);
  }
  
  //Set the module status, by the APP and the DEVICE
   public function anyStatus() {
    $module = Module::find(Input::get("module"));
    
    if ( $module ) {
      $module->status = Input::get("status");
      $module->save();
       $gcm = new GCMController;
      if(Input::get("status") == 1) $status = "ATIVADO";
      else $status = "DESATIVADO";
      $gcm->broadcast("O nó módulo ".$module->id." foi ".$status."!");
      $user = empty(Input::get("user")) ? Session::get("user") : Input::get("user");
      $this->history->save($user, $status." o módulo ".$module->id);
      $result = "success";
    } else {
      $result = "fail";
    }
    
    return Response::json(["result" => $result, "status"=>$module->status]);
  }
  
  
  //methods intended to be used by the DEVICE
  
   public function anyAddBattery(){
    $module = Module::find(Input::get("module"));
    
    if ( $module ) {
      $module->battery = Input::get("battery");
      $module->save();
       if ($module->battery == 1 || $module->battery == 2 || $module->battery == 0.5) {
        $porcentage = ($module->battery * 100)/self::BATTERY_FULL;
        $gcm = new GCMController;
        $gcm->broadcast("Bateria módulo ".$module->id." abaixo de ".$porcentage."%");
      }// TODO: send push notification
      $result = "success";
    } else {
      $result = "fail";
    }
    
    return Response::json(["result" => $result, "battery"=>$module->battery]);
  }
  
  public function anyAddSample(){
    $module = Module::find(Input::get("module"));
    
    if ( $module ) {
      $sample = new Sample;
      $sample->module = $module->id;
      $sample->value = Input::get("value");
      $sample->save();
      $result = "success";
      return Response::json(["result" => $result, "sample"=>$sample->id]);
    } else {
      $result = "fail";
      return Response::json(["result" => $result]);
    }    
  }
  
}