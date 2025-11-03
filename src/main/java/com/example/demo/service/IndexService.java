package com.example.demo.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class IndexService {


    private final IndexWriter indexWriter;
    private final IndexSearcher indexSearcher;
    private final QueryParser queryParser;

    public IndexService() throws IOException {
        String indexDirectoryPath = "index";
        Path path = Paths.get(indexDirectoryPath);
        Directory index = FSDirectory.open(path);

        StandardAnalyzer analyzer = new StandardAnalyzer();
        this.queryParser = new QueryParser("content", analyzer);

        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        this.indexWriter = new IndexWriter(index, indexWriterConfig);

        DirectoryReader directoryReader;
        if (DirectoryReader.indexExists(index)) {
            directoryReader = DirectoryReader.open(index);
            this.indexSearcher = new IndexSearcher(directoryReader);
        } else {
            // si l'index n'existe pas on le creé
            this.indexWriter.commit();
            directoryReader = DirectoryReader.open(index);
            this.indexSearcher = new IndexSearcher(directoryReader);
        }
    }
    public void indexEvaluation(String content,long id) {
        try {
            Document document = new Document();
            document.add(new TextField("content", content, Field.Store.YES));
            document.add(new LongPoint("id", id));
            document.add(new StoredField("id", id));
            this.indexWriter.addDocument(document);
            this.indexWriter.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Long> searchEvaluation(ArrayList<String> words) {
        try {
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            for (String word : words) {
                Query q = queryParser.parse(word);
                builder.add(q, BooleanClause.Occur.SHOULD);
            }
            Query finalQuery = builder.build();


            TopDocs topDocs = this.indexSearcher.search(finalQuery, Integer.MAX_VALUE);

            // Utiliser un Set pour éviter les doublons
            Set<Long> resultSet = new HashSet<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                long id = this.indexSearcher.storedFields()
                        .document(scoreDoc.doc)
                        .getField("id").numericValue().longValue();
                resultSet.add(id);
            }

            // Retourner sous forme de liste
            return new ArrayList<>(resultSet);

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
