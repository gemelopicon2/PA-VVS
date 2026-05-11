import * as actions from './actions';
import reducer from './reducer';
import * as selectors from './selectors';

export { default as PurchaseHistory } from './components/PurchaseHistory';
export { default as DeliverTickets } from './components/DeliverTickets';
export { default as PurchaseCompleted } from './components/PurchaseCompleted';

export default { actions, reducer, selectors };