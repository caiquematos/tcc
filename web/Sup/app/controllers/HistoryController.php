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
		return Response::make('stop sooping around!');
	}
    
    public function save($user, $information){
        $history = new History;
        $history->user = $user;
        $history->message = $information;
        $history->save();
    }

}