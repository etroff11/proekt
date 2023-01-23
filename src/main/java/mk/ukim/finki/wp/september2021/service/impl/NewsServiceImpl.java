package mk.ukim.finki.wp.september2021.service.impl;

import mk.ukim.finki.wp.september2021.model.News;
import mk.ukim.finki.wp.september2021.model.NewsCategory;
import mk.ukim.finki.wp.september2021.model.NewsType;
import mk.ukim.finki.wp.september2021.model.exceptions.InvalidNewsIdException;
import mk.ukim.finki.wp.september2021.repository.NewsCategoryRepository;
import mk.ukim.finki.wp.september2021.repository.NewsRepository;
import mk.ukim.finki.wp.september2021.service.NewsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final NewsCategoryRepository newsCategoryRepository;

    public NewsServiceImpl(NewsRepository newsRepository, NewsCategoryRepository newsCategoryRepository) {
        this.newsRepository = newsRepository;
        this.newsCategoryRepository = newsCategoryRepository;
    }

    @Override
    public List<News> listAllNews() {
        return this.newsRepository.findAll();
    }

    @Override
    public News findById(Long id) {
        return this.newsRepository.findById(id).orElseThrow(InvalidNewsIdException::new);
    }

    @Override
    public News create(String name, String description, Double price, NewsType type, Long category) {
        NewsCategory newsCategory = this.newsCategoryRepository.findAllById(category);
        News news = new News(name, description, price,type,newsCategory);
        return this.newsRepository.save(news);
    }

    @Override
    public News update(Long id, String name, String description, Double price, NewsType type, Long category) {
        News news = this.findById(id);
        NewsCategory newsCategory = this.newsCategoryRepository.findAllById(category);
        news.setName(name);
        news.setDescription(description);
        news.setPrice(price);
        news.setType(type);
        news.setCategory(newsCategory);
        return this.newsRepository.save(news);
    }

    @Override
    public News delete(Long id) {
        News news = this.findById(id);
        this.newsRepository.delete(news);
        return news;
    }

    @Override
    public News like(Long id) {
        News news = this.findById(id);
        news.setLikes(news.getLikes()+1);
        return this.newsRepository.save(news);
    }

    @Override
    public List<News> listNewsWithPriceLessThanAndType(Double price, NewsType type) {
        if (price == null && type == null) {
            return this.newsRepository.findAll();
        } else if (price == null) {
            return this.newsRepository.findAllByType(type).stream().filter(news -> news.getType().equals(type)).collect(Collectors.toList());
        } else if (type == null) {
            return this.newsRepository.findAllByPriceLessThan(price).stream().filter(news -> news.getPrice() < price).collect(Collectors.toList());
        } else{
            return this.newsRepository.findAllByPriceLessThanAndType(price,type);
        }
    }
}
