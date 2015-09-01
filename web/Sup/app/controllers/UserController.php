<?php

class UserController extends \BaseController {

  private $history;
  private $idUser;
  
  public function UserController(){
    $this->history = new HistoryController;
    $id = Session::get("user");
    
    if ($id == null || $id == "") {
      $this->idUser = false;
    }
    else {
      $this->idUser = Crypt::decrypt($id);
    }
  }
  
  public function anySendGcm(){
    $gcm = new GCMController;
    $user = User::find(Input::get("user"));
    $registration_ids[] = $user->gcm;
    $gcm->sendNote($registration_ids,["message"=>"GCM id was saved"]);
    return $user->gcm;
  }

    public function getIndex()
    {
          return Response::make('You can go and try /login or /register or /edit or /buddies');
    }
  
   public function anyLogin(){
        $user = User::whereEmail(Input::get("email"))->first();

        if( $user && Hash::check( Input::get("password"), $user->password ) ) {
          Session::put("user", Crypt::encrypt($user->id));
          $result = ["status"=>"success", "wasRegistered"=>"true", "user"=>$user];
          $this->history->save( $user->id, "logou no sistema" );
        } else {
          $result = ["status"=>"success", "wasRegistered"=>"false"];
        }
        
        return $result;
    }
  
    public function anyWebLogin(){
      $user = User::whereEmail(Input::get("email"))->first();
      if( $user ) {
        if ( Hash::check( Input::get("password"), $user->password ) ) {
          Session::put("user", Crypt::encrypt($user->id));
          $coordinators = Coordinator::all();
          $this->history->save( $user->id, "logou no sistema" );
          return Redirect::to("/")->with('cordenadores',["coordinators"=>$coordinators]);
        } else {
          return Redirect::to("/")->with('msg','Senha incorreta!');
        }
      } else {
        return Redirect::to("/")->with('msg','Usuário não encontrado. Por favor, realize o registro!');
      }
    }
    
  public function anyWebRegister() {
    $user = User::whereEmail(Input::get("email"))->first();

    if( $user ) {
      return Redirect::to("/")->with('msg','Usuário já registrado. Por favor, realize o login!');
    } else {
        $user = new User;
        $user->name = Input::get("name");
        $user->email = Input::get("email");
        $user->gcm = Input::get("gcm");
        $user->password = Hash::make(Input::get("password"));
        $user->save();
        $this->history->save( $user->id, "registrou no sistema" );
      return Redirect::to("/")->with('msg','Usuário registrado com sucesso!');
    }

    return Response::json($result);
  }
  
  public function anyRegister() {
    $user = User::whereEmail(Input::get("email"))->first();

    if( $user ) {
        $result = ["status"=>"success", "wasRegistered"=>"true"];
    } else {
        $user = new User;
        $user->name = Input::get("name");
        $user->email = Input::get("email");
        $user->gcm = Input::get("gcm");
        $user->password = Hash::make(Input::get("password"));
        $user->save();
        $result = ["status"=>"success", "wasRegistered"=>"false", "user"=>$user];
        $this->history->save( $user->id, "registrou no sistema" );
    }

    return Response::json($result);
  }
  
    public function anySaveGcm(){
        $user = User::find(Input::get("user"));
        
        if( $user ) {
          $user->gcm = Input::get("gcm");
          $user->save();
          $gcm = new GCMController;
          $registration_ids[] = $user->gcm;
          $gcm->sendNote($registration_ids,["message"=>"GCM ID foi salvo no servidor"]);
          $result = ["status"=>"success", "gcm"=>$user->gcm];
        } else {
          $result = ["status"=>"failed"];
        }

        return Response::json($result);
    }
    
    public function anyEdit() {
        $user = User::find(Input::get("user"));

        if($user) {
          if( Hash::check( Input::get("currentPassword"), $user->password ) ) {
            $user->email = Input::get("email");
            $user->password = Hash::make(Input::get("newPassword"));
            $user->name = Input::get("name");
            $user->save();
            $result = ["status"=>"success", "checkPassword"=>true, "user"=>$user];
            $this->history->save( $user->id, "editou seu perfil" );
          } else {
            $result = ["status"=>"success", "checkPassword"=>false];
          }
        } else {
          $result = ["status"=>"fail"];
        }
        
        return Response::json($result);
    }
  
    public function anyLoad() {
        $user = User::find(Input::get("user"));

        if($user) {
          $result = ["status"=>"success", "user"=>$user];
        } else {
          $result = ["status"=>"fail"];
        }
        
        return Response::json($result);
    }
  
  
    
}
