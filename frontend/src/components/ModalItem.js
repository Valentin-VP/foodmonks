import { Button, Modal } from 'react-bootstrap';

export const ModalItem = ({titulo, cuerpo, visible, onCancelar, onAceptar}) => {
    return(
      <>
        <Modal show={visible} onHide={onCancelar}>
          <Modal.Header closeButton>
            <Modal.Title>{titulo}</Modal.Title>
          </Modal.Header>
          <Modal.Body>{cuerpo}</Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={onCancelar}>
              Cancelar
            </Button>
            <Button variant="danger" onClick={onAceptar}>
              Aceptar
            </Button>
          </Modal.Footer>
        </Modal>
      </>
    )};
