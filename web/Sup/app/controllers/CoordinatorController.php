<?php

class CoordinatorController extends \BaseController {
  
  const BATTERY_FULL = 5.0;
  private $history;
  
  public function CoordinatorController(){
    $this->history = new HistoryController;
  }
	
	public function getIndex(){
    $coordinators = Coordinator::all();
    foreach ($coordinators as $coordinator) {
      $coordinator->numb_of_modules = Module::whereCoordinator($coordinator->id)->count();
      $coordinator->battery = ($coordinator->battery * 100)/self::BATTERY_FULL;
    }
    return View::make('coordenadores', ['coordinators'=>$coordinators]);
    //return Response::make('You can go and try /list');
	}
  
  public function anyList(){
    $coordinators = Coordinator::all();
    foreach ($coordinators as $coordinator) {
      $coordinator->numb_of_modules = Module::whereCoordinator($coordinator->id)->count();
    }
    return Response::json(["coordinators"=>$coordinators]);
  }
  
  public function anyWebList(){
    $coordinators = Coordinator::all();
    return View::make('coordenadores', ['coordinators'=>$coordinators]);
  }
    
  //Set the device status, used by the apps and either the DEVICE
  public function anySwitch() {
    $coordinator = Coordinator::find(Input::get("coordinator"));
    
    if ( $coordinator ) {
      $coordinator->status = Input::get("status");
      $coordinator->save();
      $gcm = new GCMController;
      if(Input::get("status") == 1) $status = "ATIVADO";
      else $status = "DESATIVADO";
      $gcm->broadcast("O nó coordenador ".$coordinator->id." foi ".$status."!");
      $user = empty(Input::get("user")) ? Session::get("user") : Input::get("user");
      $this->history->save($user, $status." o nó coordenador ".$coordinator->id);
      $result = "success";
    } else {
      $result = "fail";
    }
    
    return Response::json(["result" => $result, "status"=>$coordinator->status]);
  }
  
//methods intended to be used by the DEVICE
  
  //adicionar um nó coordenador
  public function anyAdd(){
    
    $coordinator = new Coordinator;
    $coordinator->network = Input::get("network");
    $coordinator->status = Input::get("status");
    $coordinator->battery = Input::get("battery");
    $coordinator->save();
    $result = "success";
    
    return Response::json(["result" => $result, "coordinator"=>$coordinator->id]);
  }
  
  public function anyRemove() {
    $coordinator = Coordinator::find(Input::get("coordinator"));
    
    if ( $coordinator ) {
      $modules = Module::whereCoordinator($coordinator->id)->get();
      foreach( $modules as $module ) {
        $samples = Sample::whereModule($module->id)->get();
        foreach( $samples as $sample ) {
          $sample->delete();
        }
        $module->delete();
      }
      $coordinator->delete();
      $result = "success";
    } else {
      $result = "fail";
    }
    
    return Response::json(["result" => $result]);
    
  }
  
  public function anyAddBattery(){
    $coordinator = Coordinator::find(Input::get("coordinator"));
    
    if ( $coordinator ) {
      $coordinator->battery = Input::get("battery");
      $coordinator->save();
      if ($coordinator->battery == 1 || $coordinator->battery == 2 || $coordinator->battery == 0.5) {
        $porcentage = ($coordinator->battery * 100)/self::BATTERY_FULL;
        $gcm = new GCMController;
        $gcm->broadcast("Bateria coordenador ".$coordinator->id." abaixo de ".$porcentage."%");
      }// TODO: send push notification
      $result = "success";
    } else {
      $result = "fail";
    }
    
    return Response::json(["result" => $result, "battery"=>$coordinator->battery]);
  }
  
  public function anyAddModule(){
    $coordinator = Coordinator::find(Input::get("coordinator"));
    
    if ( $coordinator ) {
      $module = new Module;
      $module->coordinator = $coordinator->id;
      $module->status = Input::get("status");
      $module->battery = Input::get("battery");
      $module->packFrequency = Input::get("packFrequency");
      $module->sleepTime = Input::get("sleepTime");
      $module->sleepFrequency = Input::get("sleepFrequency");
      $module->save();
      $result = "success";
      return Response::json(["result" => $result, "module"=>$module->id]);
    } else {
      $result = "fail";
      return Response::json(["result" => $result]);
    }
    
  }
  
  public function anyRemoveModule(){
    $coordinator = Coordinator::find(Input::get("coordinator"));
    
    if ( $coordinator ) {
      $module = Module::find(Input::get("module"));
      if ( $module ) {
        $samples = Sample::whereModule($module->id)->get();
        foreach( $samples as $sample ) {
          $sample->delete();
        }
        $module->delete();
        $result = "success";
      } else {
        $result = "fail";
      }
    } else {
      $result = "fail";
    }
    
    return Response::json(["result" => $result]);
  }
}