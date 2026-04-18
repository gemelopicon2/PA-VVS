import {combineReducers} from 'redux';

import * as actionTypes from './actionTypes';

const initialState = {
    movies: []
};

const catalog = (state = initialState, action) => {
    switch (action.type) {
        case actionTypes.GET_BILLBOARD_COMPLETED:
            return { ...state, movies: action.movies };
        default:
            return state;
    }
};

export default catalog;