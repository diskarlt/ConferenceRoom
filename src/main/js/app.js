import 'bootstrap/dist/css/bootstrap.min.css';
import React from 'react';
import ReactDOM from 'react-dom';
import { Alert, Button, Form, FormGroup, Input, Label } from 'reactstrap';

class App extends React.Component {
     render() {
        return <div className="main">
            Hello world
        </div>;
    }
}

// tag::render[]
ReactDOM.render(
	<App />,
	document.getElementById('react')
)
// end::render[]

