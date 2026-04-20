import {useEffect} from 'react';
import {useDispatch} from 'react-redux';

import Header from './Header';
import Body from './Body';
import Footer from './Footer';
import users from '../../users';
import catalog from '../../catalog';
import backend from '../../../backend';

const getToday = () => {
    const date = new Date();
    let day = date.getDate();
    let month = date.getMonth() + 1;
    let year = date.getFullYear();
    return `${year}-${month<10?`0${month}`:`${month}`}-${day<10?`0${day}`:`${day}`}`;
};

const App = () => {

    const dispatch = useDispatch();
    const today = getToday();
    useEffect(() => {

        const tryLoginFromServiceToken = async () => {
            const response = await backend.userService.tryLoginFromServiceToken(
                () => dispatch(users.actions.logout()));
            if (response.ok) {
                dispatch(users.actions.loginCompleted(response.payload));
            }
        }

        const getBillboard = async () => {
            dispatch(catalog.actions.clearBillboard(today));
            const response = await backend.catalogService.getBillboard(today);
            if (response.ok) {
                dispatch(catalog.actions.getBillboardCompleted(response.payload));
            }
        }

        const initApp = async () => {
                    await tryLoginFromServiceToken(); // 1º Intenta el login (y limpia el token si es inválido)
                    await getBillboard();             // 2º Pide la cartelera de forma segura
                };
        initApp();
    }, [dispatch, today]);

    return (
        <div className="d-flex flex-column min-vh-100">
            <Header/>
            <Body/>
            <Footer/>
        </div>
    );

}

export default App;
