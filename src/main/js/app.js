import 'bootstrap/dist/css/bootstrap.min.css';
import React from 'react';
import ReactDOM from 'react-dom';
import {
  Button,
  Col,
  Container,
  Form,
  FormGroup,
  Input,
  Label,
  Nav,
  Navbar,
  NavbarBrand,
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
  Row,
} from 'reactstrap';

var navStyle = {
  position: 'fixed',
  height: '56px',
  width: '100%',
  backgroundColor: '#423630',
  zIndex: '1',
}

var tableHeaderStyle = {
  position: 'fixed',
  width: '100%',
  backgroundColor: 'white',
  marginTop: '56px',
  zIndex: '1',
}

var tableBodyStyle =  {
  paddingTop: '125px',
}

var reservationStyle =  {
  backgroundColor: '#ffc107',
  padding: '10px',
  borderLeft: '1px solid #dee2e6'
}

var datePickerStyle = {
  maxWidth: '150px',
  width: '150px',
  padding: '15px 10px',
  verticalAlign: 'bottom',
  textAlign: 'center',
  borderBottom: '1px solid #dee2e6',
}

var roomListStyle = {
  padding: '20px 0px 20px 0px',
  verticalAlign: 'bottom',
  textAlign: 'center',
  borderBottom: '1px solid #dee2e6',
}

var timeRangeStyle = {
  maxWidth: '150px',
  width: '150px',
  textAlign: 'center',
  padding: '0.75rem',
  borderTop: '1px solid #dee2e6',
}
var timeLineStyle = {
  borderTop: '1px solid #dee2e6',
  padding: '10px',
  borderLeft: '1px solid #dee2e6'
}

class ReservationBoard extends React.Component {
  render() {
    const roomNames = ["회의실 A", "회의실 B", "회의실 C", "회의실 D", "회의실 E", "회의실 F", "회의실 G"];
    const listRooms = roomNames.map((roomName, index) =>
        <Col className={'font-weight-bold'} style={roomListStyle} key={index}>
          {roomName}
        </Col>
    );

    const TableHeader = <Row style={{width: "100%"}}>
                          <Col style={datePickerStyle}>
                            <Input name="date" value={this.props.date} onChange={this.props.onChange}/>
                          </Col>
                          {listRooms}
                        </Row>;

    let timeRanges = [];
    for(var i=0; i<48; ++i) {
      let timeRange = "";
      timeRange += parseInt(i/2) + ":" + ((i%2)?"30":"00") + " ~ " + parseInt((i+1)/2) + ":" + ((i%2)?"00":"30") ;
      timeRanges.push(timeRange);
    }

    const TableBody = timeRanges.map((timeRange, index) =>
      <Row key={index}>
        <Col key={timeRange.toString} className={'font-weight-bold'} style={timeRangeStyle}>{timeRange}</Col>
        {roomNames.map((roomName) => {
          var ret = (<Col style={timeLineStyle}/>);
          Array.prototype.forEach.call(this.props.reservations, reservation => {
            let startTime = (parseInt(index/2)<10?"0":"") + parseInt(index/2) + ":" + ((index%2)?"30":"00");
            let endTime = (parseInt((index+1)/2)<10?"0":"") + parseInt((index+1)/2) + ":" + ((index%2)?"00":"30");
            if(reservation.roomName === roomName &&
               reservation.startTime <= startTime &&
               reservation.endTime >= endTime) {
              if(reservation.startTime === startTime) {
                ret = (<Col className='font-weight-bold' style={reservationStyle}>
                          <Button id={reservation.id} onClick={()=>this.props.onCancel(reservation.id)} close />
                          { reservation.repeat?<div>(반복 {reservation.repeat}회)</div>: null }
                          <div>{reservation.userName}</div>
                          <div>{reservation.startTime} ~ {reservation.endTime}</div>
                       </Col>);
              } else {
                ret = (<Col className='font-weight-bold' style={{...reservationStyle, borderTop: '0'}} />)
              }
            }
          })
          return ret;
        })}
      </Row>
    );

    return <Container style={{maxWidth:"100%"}}>
      <div style={tableHeaderStyle}>{TableHeader}</div>
      <div style={tableBodyStyle}>{TableBody}</div>
    </Container>
  }
}

class App extends React.Component {
  componentDidUpdate() {
    window.scrollTo(0,850);
  }

  constructor() {
    super();

    this.toggle = this.toggle.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleCancel = this.handleCancel.bind(this);

    var todayDate = new Date().toISOString().slice(0,10);
    this.state = {
      roomName: "회의실 A",
      userName: "",
      date: todayDate,
      repeat: 0,
      startTime: "",
      endTime: "",
      modal: false,
      reservations: {}
    };

    this.getReservationData = this.getReservationData.bind(this);
  }

  getReservationData(date) {
    var year = date.slice(0, 4);
    var month = date.slice(5, 7);
    var day = date.slice(8);
    var url = 'http://localhost:8080/reservations'
    url += '?year=' + year;
    url += '&month=' + month;
    url += '&day=' + day;
    fetch(url, {
      method: 'GET',
      mode: 'cors',
      headers: {'Content-Type':'application/json'},
    }).then(response => response.json())
    .then(data => {
      this.setState({
        reservations: data
      });
    });
  }

  componentDidMount() {
    this.getReservationData(this.state.date);
  }

  handleChange(e) {
    this.setState({
      [e.target.name]: e.target.value
    })

    if(e.target.name === "date") {
      if(e.target.value.match("[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]"))
        this.getReservationData(e.target.value);
    }
  }

  handleSubmit(e) {
    e.preventDefault();
    var req = {
      "roomName" : this.state.roomName,
      "userName" : this.state.userName,
      "date" : this.state.date,
      "repeat" : this.state.repeat,
      "startTime" : this.state.startTime,
      "endTime" : this.state.endTime,
    }

    fetch('http://localhost:8080/reservations', {
      method: 'POST',
      mode: 'cors',
      headers: {'Content-Type':'application/json'},
      body: JSON.stringify(req),
    }).then(response => {
      if(response.status === 200)
        alert("예약이 완료되었습니다.");
      else
        alert("예약이 실패하였습니다.");
      this.getReservationData(this.state.date);
    });
  }

  handleCancel(id) {
    fetch('http://localhost:8080/reservations/'+id, {
      method: 'DELETE',
      mode: 'cors',
    }).then(() => {
      this.getReservationData(this.state.date);
    })
  }

  toggle() {
    this.setState({
      modal: !this.state.modal
    });
  }

  render() {
    return <div className="main">
      <div style={navStyle}>
      <Navbar dark expand="md">
        <NavbarBrand href="/">회의실</NavbarBrand>
        <Nav className="ml-auto" navbar>
          <Button outline color="warning" onClick={this.toggle}>예약하기</Button>
        </Nav>
        <Modal isOpen={this.state.modal} toggle={this.toggle} className={this.props.className}>
          <Form onSubmit={this.handleSubmit}>
            <FormGroup>
              <ModalHeader toggle={this.toggle}>회의실 예약</ModalHeader>
              <ModalBody>
                <Label for="roomName">회의실</Label>
                <Input type="select" name="roomName" value={this.state.roomName} onChange={this.handleChange}>
                  <option>회의실 A</option>
                  <option>회의실 B</option>
                  <option>회의실 C</option>
                  <option>회의실 D</option>
                  <option>회의실 E</option>
                  <option>회의실 F</option>
                  <option>회의실 G</option>
                </Input>
                <Label for="userName">예약자명</Label>
                <Input name="userName" value={this.state.userName} onChange={this.handleChange} placeholder="예약자명" />
                <Label for="date">예약일자</Label>
                <Input name="date" value={this.state.date} onChange={this.handleChange} placeholder="예약일자" />
                <Label for="startTime">시작시간</Label>
                <Input name="startTime" value={this.state.startTime} onChange={this.handleChange} placeholder="시작시간" />
                <Label for="endTime">종료시간</Label>
                <Input name="endTime" value={this.state.endTime} onChange={this.handleChange} placeholder="종료시간" />
                <Label for="repeat">반복하기</Label>
                <Input name="repeat" value={this.state.repeat} onChange={this.handleChange} placeholder="반복횟수" />
              </ModalBody>
              <ModalFooter>
                <Button type="submit" color="primary" onClick={this.toggle}>예약</Button>{' '}
                <Button color="secondary" onClick={this.toggle}>취소</Button>
              </ModalFooter>
            </FormGroup>
          </Form>
        </Modal>
      </Navbar>
      </div>
      <ReservationBoard date={this.state.date} onChange={this.handleChange} onCancel={this.handleCancel} reservations={this.state.reservations}/>
    </div>;
  }
}

// tag::render[]
ReactDOM.render(
	<App />,
	document.getElementById('react')
)
// end::render[]

