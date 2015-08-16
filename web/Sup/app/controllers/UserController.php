<?php

class UserController extends \BaseController {

    private $history;

    public function UserController(){
        $this->history = new HistoryController;
    }
	
	public function getIndex()
	{
        return Response::make('You can go and try /login or /register or /edit or /buddies');
	}
    
    public function anyLogin(){
        $json = json_decode(Input::get("json"));
        $user = User::whereEmail($json->email)->first();

        if( $user && Hash::check( $json->password, $user->password ) ) {
            $user->save();
            $result = ["status"=>"success", "wasRegistered"=>"true", "user"=>$user];
            //$this->history->save( $user->id, "you lodded in on the system" );
        } else {
            $result = ["status"=>"success", "wasRegistered"=>"false"];
        }
        
        return Response::json($result);
    }
    
    public function anyRegister() {
        $json = json_decode(Input::get("json"));
        $user = User::whereEmail($json->email)->first();
        
        if( $user ) {
            $result = ["status"=>"success", "wasRegistered"=>"true"];
        } else {
            $user = new User;
            $user->name = $json->name;
            $user->email = $json->email;
            $user->password = Hash::make($json->password);
            $user->save();
            $result = ["status"=>"success", "wasRegistered"=>"false", "user"=>$user];
            //$this->history->save( $user->id, "you signed up to the system" );
        }

        return Response::json($result);
    }
    
    public function anyEdit() {
        $json = json_decode(Input::get("json"));
        $user = User::whereEmail($json->email)->first();

        if( Hash::check( $json->password, $user->password ) ) {
            $user->email = $json->email;
            $user->password = Hash::make($json->password);
            $user->name = $json->name;
            $user->birthdate = $json->birthdate;
            $user->save();
            $result = ["status"=>"success", "registered"=>"true"];
            $this->history->save( $user->id, "you edited your profile" );
        } else {
            $result = ["status"=>"success", "registered"=>"false"];
        }
        
        return Response::json($result);
    }
    
    public function anyBuddies() {
        $json = json_decode(Input::get("json"));
        $user = User::whereEmail($json->email)->first();
        $buddies = Buddy::whereUser($user->id)->whereStatus('F')->get();
        $result = ["status"=>"success", "buddies"=>$buddies];

        return Response::json($result);
    }

}