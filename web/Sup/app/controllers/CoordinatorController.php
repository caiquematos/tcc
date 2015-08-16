<?php

class CoordinatorController extends \BaseController {
	
	public function getIndex(){
    return Response::make('You can go and try /list');
	}
  
  public function anyList(){
    $coordinators = Coordinator::all();
    foreach ($coordinators as $coordinator) {
      $coordinator->numb_of_modules = Module::whereCoordinator($coordinator->id)->count();
    }
    return Response::json(["coordinators"=>$coordinators]);
  }
  
  //Set the device status, used by the apps and either the DEVICE
  public function anySwitch() {
    $json = json_decode(Input::get("json"));
    $coordinator = Coordinator::find($json->coordinator);
    
    if ( $coordinator ) {
      $coordinator->status = $json->status;
      $coordinator->save();
      $result = "success";
    } else {
      $result = "fail";
    }
    
    return Response::json(["result" => $result, "status"=>$coordinator->status]);
  }
  
//methods intended to be used by the DEVICE
  
  //adicionar um nó coordenador
  public function anyAdd(){
    $json = json_decode(Input::get("json"));
    
    $coordinator = new Coordinator;
    $coordinator->network = $json->network;
    $coordinator->status = $json->status;
    $coordinator->battery = $json->battery;
    $coordinator->save();
    $result = "success";
    
    return Response::json(["result" => $result, "coordinator"=>$coordinator->id]);
  }
  
  public function anyRemove() {
    $json = json_decode(Input::get("json"));
    $coordinator = Coordinator::find($json->coordinator);
    
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
    $json = json_decode(Input::get("json"));
    $coordinator = Coordinator::find($json->coordinator);
    
    if ( $coordinator ) {
      $coordinator->battery = $json->battery;
      $coordinator->save();
      if ($coordinator->battery == 2 || $coordinator->battery == 1) // TODO: send push notification
      $result = "success";
    } else {
      $result = "fail";
    }
    
    return Response::json(["result" => $result, "battery"=>$coordinator->battery]);
  }
  
  public function anyAddModule(){
    $json = json_decode(Input::get("json"));
    $coordinator = Coordinator::find($json->coordinator);
    
    if ( $coordinator ) {
      $module = new Module;
      $module->coordinator = $coordinator->id;
      $module->status = $json->status;
      $module->battery = $json->battery;
      $module->packFrequency = $json->packFrequency;
      $module->sleepTime = $json->sleepTime;
      $module->sleepFrequency = $json->sleepFrequency;
      $module->save();
      $result = "success";
      return Response::json(["result" => $result, "module"=>$module->id]);
    } else {
      $result = "fail";
      return Response::json(["result" => $result]);
    }
    
  }
  
  public function anyRemoveModule(){
    $json = json_decode(Input::get("json"));
    $coordinator = Coordinator::find($json->coordinator);
    
    if ( $coordinator ) {
      $module = Module::find($json->module);
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