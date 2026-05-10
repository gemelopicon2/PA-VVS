import { useState, useRef } from 'react';
import Card from 'react-bootstrap/Card';
import Form from 'react-bootstrap/Form';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Button from 'react-bootstrap/Button';
import Alert from 'react-bootstrap/Alert';

import { Errors, BackLink } from '../../common';
import backend from '../../../backend';

const DeliverTickets = () => {

    const [purchaseId, setPurchaseId] = useState('');
    const [creditCard, setCreditCard] = useState('');
    const [formValidated, setFormValidated] = useState(false);
    const [backendErrors, setBackendErrors] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);
    const formRef = useRef();

    const handleSubmit = async (event) => {
        event.preventDefault();
        setSuccessMessage(null);

        if (formRef.current.checkValidity()) {
            const response = await backend.shoppingService.deliverTickets(purchaseId, creditCard);
            if (response.ok) {
                setSuccessMessage('Entradas entregadas correctamente');
                setPurchaseId('');
                setCreditCard('');
                setFormValidated(false);
                setBackendErrors(null);
            } else {
                setBackendErrors(response.payload);
            }
        } else {
            setBackendErrors(null);
            setFormValidated(true);
        }
    };

    return (
        <div>
            <BackLink />
            <Card className="bg-light border-dark mt-3">
                <Card.Header as="h5">Entregar entradas</Card.Header>
                <Card.Body>
                    <Form ref={formRef} noValidate validated={formValidated} onSubmit={handleSubmit}>
                        <Form.Group as={Row} className="mb-3" controlId="purchaseId">
                            <Form.Label column md={3}>ID de compra</Form.Label>
                            <Col md={4}>
                                <Form.Control
                                    type="text"
                                    value={purchaseId}
                                    onChange={e => setPurchaseId(e.target.value)}
                                    required
                                    autoFocus
                                />
                                <Form.Control.Feedback type="invalid">Campo obligatorio</Form.Control.Feedback>
                            </Col>
                        </Form.Group>

                        <Form.Group as={Row} className="mb-3" controlId="creditCard">
                            <Form.Label column md={3}>Tarjeta bancaria</Form.Label>
                            <Col md={4}>
                                <Form.Control
                                    type="text"
                                    value={creditCard}
                                    onChange={e => setCreditCard(e.target.value)}
                                    required
                                />
                                <Form.Control.Feedback type="invalid">Campo obligatorio</Form.Control.Feedback>
                            </Col>
                        </Form.Group>

                        <Form.Group as={Row}>
                            <Col md={{ span: 4, offset: 3 }}>
                                <Button type="submit">Entregar</Button>
                            </Col>
                        </Form.Group>
                    </Form>
                </Card.Body>
            </Card>
            <Errors errors={backendErrors} onClose={() => setBackendErrors(null)} />
            {successMessage && (
                <Alert variant="success" className="mt-3" onClose={() => setSuccessMessage(null)} dismissible>
                    {successMessage}
                </Alert>
            )}
        </div>
    );
};

export default DeliverTickets;