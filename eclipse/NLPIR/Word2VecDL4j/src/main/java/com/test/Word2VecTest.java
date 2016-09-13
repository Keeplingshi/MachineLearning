package com.test;

import java.io.File;
import java.io.IOException;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.io.ClassPathResource;

public class Word2VecTest {
    
    public static void main(String[] args){
    	System.out.println("1111111111111");
    	new Word2VecTest().test();
    }
    
    public void test()
    {
		try {
			
	        SentenceIterator iter = new LineSentenceIterator(new File("test.txt"));
	        
	        TokenizerFactory t = new DefaultTokenizerFactory();
	        t.setTokenPreProcessor(new CommonPreprocessor());
	        
	        Word2Vec vec = new Word2Vec.Builder()
	                .minWordFrequency(5)
	                .iterations(1)
	                .layerSize(100)
	                .seed(42)
	                .windowSize(5)
	                .iterate(iter)
	                .tokenizerFactory(t)
	                .build();
	        
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    }
	
}
