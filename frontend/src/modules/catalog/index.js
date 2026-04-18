import * as actions from './actions';
import * as actionTypes from './actionTypes';
import reducer from './reducer';
import * as selectors from './selectors';
import Billboard from './components/Billboard';

const catalog = {actions, actionTypes, reducer, selectors, Billboard };

export default catalog;
export { actions, actionTypes, reducer, selectors, Billboard };