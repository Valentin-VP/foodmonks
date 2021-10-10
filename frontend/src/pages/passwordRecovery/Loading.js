import "./Loading.css"

export default function Loading(){
    return (
    <div className="spinner">
        <span>Cargando...</span>
        <div className="half-spinner"></div>
    </div>
    );
}