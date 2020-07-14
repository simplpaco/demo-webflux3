import React, {Component} from 'react';
import {interval} from 'rxjs';
import {startWith, switchMap} from 'rxjs/operators';

import {Row, Col, Table, Button} from "antd";


class MovieList extends Component {
    state = {
        profiles: [],
        isLoading: false,
    }

    async componentDidMount() {
        this.setState({isLoading: true});
        /*const response = await fetch('http://localhost:8080/haha');
        const data = await response.json();
        this.setState(
            {
                //profiles: data,
                profiles: [data.movies, ...this.state.profiles],
                sLoading: false
            });*/

        fetch('http://localhost:8080/haha');
        this.setState({isLoading: false});

        const eventSource = new EventSource('http://localhost:8080/sse/movies');
        eventSource.onopen = (event) => console.log('open', event);
        eventSource.onmessage = (event) => {
            const profile = JSON.parse(event.data).source;
            //this.state.profiles.push(profile.movies);
            //this.setState({profiles: this.state.profiles});
            this.setState(
                {
                    //profiles: data,
                    profiles: [...profile.movies, ...this.state.profiles.slice(0,10)],
                    isLoading: false
                });
            console.log(this.state.profiles);
        };
        eventSource.onerror = (event) => console.log('error', event);
    }

    /*async componentDidMount() {
        this.setState({isLoading: true});

        const request = interval(1000).pipe(
            startWith(0),
            switchMap(async () =>
                fetch('http://localhost:8080/haha')
                    .then(
                        (response) => response.json()
                    )
            ));

        request.subscribe(data => {
            //console.log(data);
            this.setState(
                {
                    profiles: [data.movies, ...this.state.profiles],
                    isLoading: false
                });
            //console.log(this.state.profiles);
            this.state.profiles.map(value => {
                console.log(value[0])
            })
        })
    }*/

    render() {
        const columns = [
            {
                title: 'ID',
                dataIndex: 'score',
                key: 'score',
            },
            {
                title: 'Name',
                dataIndex: 'name',
                key: 'name',
            }
        ];
        if (this.state.isLoading) {
            return <p>Loading...</p>;
        }
        return (

            <div>
                <h2>List</h2>
                <Row>
                    <Col span={12}>
                        {this.state.profiles.map(p =>
                            <div key={p.name}>
                                {p.name}
                            </div>
                        )}
                    </Col>
                    <Col span={12}>
                        <Button onClick={this.pause}>Pause</Button>
                        <Button onClick={this.resume}>Resume</Button>
                        <Table dataSource={this.state.profiles} columns={columns} />;
                    </Col>
                </Row>



            </div>
        );
    }
}

export default MovieList;