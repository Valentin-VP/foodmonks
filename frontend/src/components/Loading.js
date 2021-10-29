import styled from "styled-components";

const Styles = styled.div`
  .spinner {
    position: relative;
    width: 300px;
    height: 300px;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: transparent;
  }
  
  .spinner span {
    font-size: 2rem;
    animation: fade 1s linear 0s;
    padding-right: 1rem;
  }
  
  .half-spinner {
    width: 50px;
    height: 50px;
    border: 3px solid #03fc4e;
    border-top: 3px solid transparent;
    border-radius: 50%;
    animation: spin 0.8s linear 0s infinite;
  }
  
  @keyframes spin {
    from {
      transform: rotate(0);
    }
    to {
      transform: rotate(360deg);
    }
  }
  
  @keyframes fade {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }
`;

export const Loading = () =>{return(
    <Styles>
        <div className="spinner">
            <span>Cargando...</span>
            <div className="half-spinner"></div>
        </div>
    </Styles>
)}