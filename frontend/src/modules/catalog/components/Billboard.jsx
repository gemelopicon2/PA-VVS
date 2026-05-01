import { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import Movies from './Movies';
import * as selectors from '../selectors';
import backend from '../../../backend';
import * as actions from '../actions';
import DateSelector from "./DateSelector";

const toIsoDate = (date) => {
    const d = date.getDate();
    const m = date.getMonth() + 1;
    const y = date.getFullYear();
    return `${y}-${m < 10 ? `0${m}` : m}-${d < 10 ? `0${d}` : d}`;
};

const Billboard = () => {
    const movies = useSelector(selectors.getMovies);
    const billboardDate = useSelector(selectors.getBillboardDate);
    const dispatch = useDispatch();

    const handleBillboardDateChange = async date => {
        dispatch(actions.clearBillboard(date));
        const response = await backend.catalogService.getBillboard(date);
        if (response.ok) {
            dispatch(actions.getBillboardCompleted(response.payload));
        }
    };

    useEffect(() => {
        handleBillboardDateChange(toIsoDate(new Date()));
    }, []);

    return (
        <div>
            <DateSelector
                id="billboardDate"
                className="mb-2 w-auto"
                value={billboardDate || ""}
                onChange={e => handleBillboardDateChange(e.target.value)}
            />
            <Movies movies={movies} />
        </div>
    );
};

export default Billboard;