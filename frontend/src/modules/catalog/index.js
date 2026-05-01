import * as actions from './actions';
import * as actionTypes from './actionTypes';
import reducer from './reducer';
import * as selectors from './selectors';
import Billboard from './components/Billboard';
import MovieDetails from './components/MovieDetails';
import SessionDetails from './components/SessionDetails';



const catalog = {actions, actionTypes, reducer, selectors, Billboard, MovieDetails, SessionDetails};

export default catalog;
export { actions, actionTypes, reducer, selectors, Billboard, MovieDetails, SessionDetails };
