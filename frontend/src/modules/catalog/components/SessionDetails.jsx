import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router';
import { useSelector } from 'react-redux';
import { FormattedDate, FormattedTime } from 'react-intl';
import backend from '../../../backend';
import users from '../../users';
import { BackLink, Errors } from '../../common';

const SessionDetails = () => {
    const loggedIn = useSelector(users.selectors.isLoggedIn);
    const [session, setSession] = useState(null);

    const [tickets, setTickets] = useState(1);
    const [creditCard, setCreditCard] = useState('');
    const [backendErrors, setBackendErrors] = useState(null);

    const { id } = useParams();
    const sessionId = Number(id);
    const navigate = useNavigate();

    useEffect(() => {
        const findSessionById = async sessionId => {
            if (!Number.isNaN(sessionId)) {
                const response = await backend.catalogService.findSessionById(sessionId);
                if (response.ok) {
                    setSession(response.payload);
                } else {
                    setBackendErrors(response.payload);
                }
            }
        }
        findSessionById(sessionId);
    }, [sessionId]);

    const handleSubmit = async (event) => {
        event.preventDefault();

        const response = await backend.shoppingService.buy(
            sessionId,
            tickets,
            creditCard
        );

        if (response.ok) {
            navigate(`/shopping/purchase-completed/${response.payload.id}`);
        } else {
            setBackendErrors(response.payload);
        }
    };

    if (!session && backendErrors) {
        return (
            <div>
                <BackLink />
                <div className="mt-3">
                    <Errors errors={backendErrors} onClose={() => navigate('/')} />
                </div>
            </div>
        );
    }

    if (!session) {
        return null;
    }

    return (
        <div>
            <BackLink />

            <div className="mt-3">
                <Errors errors={backendErrors} onClose={() => setBackendErrors(null)} />
            </div>

            <div className="card mt-3">
                <div className="card-body">
                    <h2 className="card-title">{session.movieTitle}</h2>
                    <ul className="list-group list-group-flush mb-3">
                        <li className="list-group-item"><strong>Duración:</strong> {session.duration} minutos</li>
                        <li className="list-group-item"><strong>Precio:</strong> {session.price} €</li>
                        <li className="list-group-item">
                            <strong>Día:</strong> <FormattedDate value={new Date(session.date)} />
                        </li>
                        <li className="list-group-item">
                            <strong>Hora:</strong> <FormattedTime value={new Date(session.date)} />
                        </li>
                        <li className="list-group-item"><strong>Sala:</strong> {session.roomName}</li>
                        <li className="list-group-item"><strong>Entradas disponibles:</strong> {session.availableTickets}</li>
                    </ul>

                    {loggedIn && session.availableTickets > 0 && (
                        <div className="mt-4 border-top pt-3">
                            <h4>Comprar Entradas</h4>
                            <form onSubmit={handleSubmit}>
                                <div className="mb-3">
                                    <label htmlFor="tickets" className="form-label">Número de entradas (1-10):</label>
                                    <input
                                        type="number"
                                        className="form-control"
                                        id="tickets"
                                        min="1"
                                        max="10"
                                        value={tickets}
                                        onChange={(e) => setTickets(Number(e.target.value))}
                                        required
                                    />
                                </div>
                                <div className="mb-3">
                                    <label htmlFor="creditCard" className="form-label">Tarjeta Bancaria:</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="creditCard"
                                        value={creditCard}
                                        onChange={(e) => setCreditCard(e.target.value)}
                                        required
                                    />
                                </div>
                                <button type="submit" className="btn btn-primary">
                                    Comprar Entradas
                                </button>
                            </form>
                        </div>
                    )}

                    {loggedIn && session.availableTickets === 0 && (
                        <div className="mt-4 alert alert-warning">
                            Lo sentimos, las entradas están agotadas.
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default SessionDetails;