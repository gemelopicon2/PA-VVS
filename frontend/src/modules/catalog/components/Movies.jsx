import React from 'react';
import { FormattedDate, FormattedTime } from 'react-intl';

const Movies = ({ movies }) => {
    if (!movies || movies.length === 0) {
        return <div>No hay películas en cartelera para hoy.</div>;
    }

    return (
        <div className="billboard-movies">
            {movies.map(movie => (
                <div key={movie.id} className="movie-card">
                    <h3>{movie.name}</h3>
                    <div className="sessions">
                        {movie.sessions.map(session => (
                            <span key={session.id} className="session-time">
                                <FormattedDate value={new Date(session.date)} />{' '}
                                <FormattedTime value={new Date(session.date)} />
                            </span>
                        ))}
                    </div>
                </div>
            ))}
        </div>
    );
};
export default Movies;