[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]


## Table of Contents

* [About the Project](#about-the-project)
  * [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Usage](#usage)
* [Contributing](#contributing)
* [License](#license)
* [Contact](#contact)
* [Acknowledgements](#acknowledgements) 

## About The Project

Project developed for educational purposes. It is an implementation of scraping 
with the use of HtmlUnit library. A cron job is set, with the use of Quartz Scheduler, and scraping starts automatically
in a specified time every day. As soon as the scheduler starts it scrapes [Meteo](https://www.meteo.gr) website and fetches all 
weather predictions (time, temperature, humidity, wind speed, phenomenon) for every day - in 3-hour time intervals - 
of every city in Greece. The values scraped are saved in a relational database. 
Endpoints are then exposed so that the end user can view the meteorological predictions of current day and also the 
upcoming days for every individual city and day available. 
Thus this application provides actual meteorological predictions.
 
### Built With

* [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
* [Maven](https://maven.apache.org/)
* [HtmlUnit](https://htmlunit.sourceforge.io/)
* [Hibernate 5.4](https://hibernate.org/orm/releases/5.4/)
* [Quartz Scheduler](http://www.quartz-scheduler.org/)
* [Grizzly HttpServer](https://javaee.github.io/grizzly/)
* [Jersey 2](https://eclipse-ee4j.github.io/jersey/)
* [Apache Lucene](https://lucene.apache.org/)
* [Docker](https://www.docker.com/)
* [Docker compose](https://docs.docker.com/compose/)
* [Liquibase](https://www.liquibase.com/)


## Getting Started

A Cron job has been set up so that the application runs at 23:55 every night. You can change that in ScrapeScheduler
class and make it run at any time desired, even multiple times a day.

The first time that the application is run there will not be any predictions scraped so the endpoints will not return 
any data. For this reason the application is shipped with sample data for one city - Thessaloniki and the endpoints
can be tested before the cron job is fired (see {linke}Usage below) 

### Prerequisites
This application utilizes docker and docker compose for deployment. Install [Docker](https://docs.docker.com/get-docker/) 
and [Docker Compose](https://docs.docker.com/compose/install/) on your machine.
You can also use [Postman](https://www.postman.com/downloads/) for the requests. Make sure you download 
Postman version 7.x.x. After installation open Settings section and enable "Use next generation URL processing" option.
This way encoding for Greek characters is enabled in URL path.


### Installation

1. Clone the repo
```sh
git clone https://github.com/StefanosAnastasiou/meteo-scraper-api.git
```
2. cd into the directory

3. Go to docker-compose.yml file and adapt the ```DATABASE_USERNAME``` and ```DATABASE_PASSWORD```
 
4. Build the application 
```sh
mvn clean install
```

5. Build application image
```sh
docker build . -t meteo-scraper-api:latest
```

6. Run docker compose to start the containers - postgres db, nginx and the application itself
```sh 
docker-compose -up
``` 

## Usage

Three endpoints are exposed for the weather predictions. The first one is fetching all predictions for a specified 
city from the time the request is made and onwards (e.g. if scraping is scheduled to start at 18:25, for the first day
it will scrape values until midnight). 
One endpoint is fetching all predictions for a city that are available in the database from the time the request was 
done and onwards, one endpoint is fetching predictions for a specific city and date
and the other is fetching predictions for a city, for a specific time a day. Various tools can be used for the request, 
postman, insomnia, curl etc.

The following requests can be used to test the endpoint with the sample data that
that ship with the application

Fetches all predictions available for a Thessaloniki:
```sh
http://localhost/predictions/ΘΕΣΣΑΛΟΝΙΚΗ
```

Fetches predictions for Thessaloniki for a specified day:
```sh
http://localhost/predictions/ΘΕΣΣΑΛΟΝΙΚΗ/2025-12-20
```

fetches predictions for Thessaloniki, for a given time of a day: 
```sh
http://localhost/predictions/ΘΕΣΣΑΛΟΝΙΚΗ/2025-12-20/21:00:00
```

Sample data are produced by Liquibase for the current date and onwards the application is run. For testing, make sure to
adjust the dates above. 

All the city names are also kept in a Lucene index so that they can be searched fast without having to query the 
database. For this operation an endpoint is exposed e.g. the following request:

```sh
http://localhost/suggest/ΒΟΛ
```

will return all the available cities that contain "ΒΟΛ"

```yaml
[
  {
    "city": "ΒΟΛΟΣ",
    "id": "33"
  },
  {
    "city": "ΜΕΓΑΛΗ ΒΟΛΒΗ",
    "id": "253"
  }
]
```

This makes easy future frontend applications (e.g. in search boxes) to search for cities fast without having to query
the database.


## Contributing

Feel like you wanna have some fun? Or suggest improvements of any kind?

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


## License

Distributed under the MIT License. See `LICENSE` for more information.


## Contact

Stefanos Anastasiou - emperor_stef@yahoo.gr

Project Link: [https://github.com/stefanosAnastasiou/meteo-scraper-api](https://github.com/stefanosAnastasiou/meteo-scraper-api)

## Acknowledgements
* [GitHub Emoji Cheat Sheet](https://www.webpagefx.com/tools/emoji-cheat-sheet)
* [Img Shields](https://shields.io)
* [Choose an Open Source License](https://choosealicense.com)

[license-shield]: https://img.shields.io/badge/license-MIT-green    
[license-url]: https://choosealicense.com/licenses/mit/
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=flat-square&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/stefanosanastasiou/
