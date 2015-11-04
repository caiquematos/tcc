<?php

class HistoryController extends \BaseController {

	/**
	 * Display a listing of the resource.
	 * GET /history
	 *
	 * @return Response
	 */
	public function getIndex()
	{
		return Response::make('stop snooping around!');
	}
  
  public function anyList(){
    $histories = History::orderBy("created_at", "DESC")->take(30)->get();
    return Response::json(["histories"=>$histories]);
  }
    
    public function save($user, $information){
        $history = new History;
        $history->user = $user;
        $history->message = $information;
        $history->save();
    }

}