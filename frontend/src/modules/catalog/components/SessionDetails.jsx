import { useState, useEffect } from 'react';
import { useParams } from 'react-router';
import { useSelector } from 'react-redux';
import { FormattedDate, FormattedTime } from 'react-intl';
import backend from '../../../backend';
import users from '../../users';
import { BackLink } from '../../common';

const SessionDetails = () => {
    const loggedIn = useSelector(users.selectors.isLoggedIn);

    const [session, setSession] = useState(null);
    const { id } = useParams();
    const sessionId = Number(id);

    useEffect(() => {
        const findSessionById = async sessionId => {
            if (!Number.isNaN(sessionId)) {
                const response = await backend.catalogService.findSessionById(sessionId);
                if (response.ok) {
                    setSession(response.payload);
                }
            }
        }
        findSessionById(sessionId);
    }, [sessionId]);

    if (!session) {
        return null;
    }

    return (
        <div>
            <BackLink />
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

                    {loggedIn && (
                        <div className="mt-4">
                            <div className="alert alert-secondary">
                                [Formulario de compra de entradas en construcción]
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default SessionDetails;